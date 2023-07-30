package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public UserDto deactivateUser(UserDto userDto) {
        validate(userDto.getId());
        deleteEvents(userDto);
        return userDto;
    }

    private void deleteEvents(UserDto userDto) {
        List<Event> userEvents = eventRepository.findAllByUserId(userDto.getId());
        for (Event event : userEvents) {
            if (event.getOwner().getId() == userDto.getId()) {
                eventRepository.deleteById(event.getId());
            }
        }
    }

    private void validate(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException(MessageFormat.format("User {0} does not exist!", userId));
        }
    }
}
