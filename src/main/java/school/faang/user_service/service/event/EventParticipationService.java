package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(Long eventId, Long userId) { //1
        validateEventId(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        for (User user : users) {
            if (user.getId() == userId) {
                throw new DataValidationException("You are registered already!");
            }
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) { //2
        if (checkThereIsUserInEvent(eventId, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new DataValidationException("You are not registered");
        }
    }

    public boolean checkThereIsUserInEvent(long eventId, long userId) { //2.1
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public List<UserDto> getListOfParticipant(Long eventId) { //3
        validateEventId(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        List<UserDto> userDto = new ArrayList<>();
        for (User user : users) {
            userDto.add(userMapper.toDto(user));
        }
        return userDto;
    }

    public int getCountRegisteredParticipant(Long eventId) { //4
        validateEventId(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }

    private void validateEventId(Long eventId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new DataValidationException("There is not event with this ID!");
        }
    }
}