package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.model.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public void registerParticipant(UserDto userDto, EventDto eventDto) {
        if (isParticipantRegistered(userDto, eventDto)) {
            throw new DataValidationException("Пользователь уже зарегистрирован на событие");
        }
        eventParticipationRepository.register(userDto.id(), eventDto.id());
    }

    public void unregisterParticipant(UserDto userDto, EventDto eventDto) {
        if (isParticipantNotRegistered(userDto, eventDto)) {
            throw new DataValidationException("Пользователь не был зарегистрирован на событие");
        }
        eventParticipationRepository.unregister(userDto.id(), eventDto.id());
    }

    public List<UserDto> getParticipants(long eventId) {
        return userMapper.toDtos(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isParticipantRegistered(UserDto userDto, EventDto eventDto) {
        User user = userService.getUserById(userDto.id());
        List<Long> participantsIds = eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()).stream()
                .map(User::getId)
                .toList();
        return participantsIds.contains(user.getId());
    }

    private boolean isParticipantNotRegistered(UserDto userDto, EventDto eventDto) {
        return !isParticipantRegistered(userDto, eventDto);
    }
}
