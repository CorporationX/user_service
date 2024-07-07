package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        List<User> usersForEvent = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        if (!usersForEvent.isEmpty()) {
            log.warn("User already registered for event");
            throw new IllegalArgumentException("Пользователь уже зарегистрирован на событие");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unRegisterParticipant(long eventId, long userId) {
        List<User> usersForEvent = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        if (usersForEvent.stream().findFirst().isPresent()) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            log.error("user not registered on event");
            throw new IllegalArgumentException("Пользователь не регистрировался на событие");
        }
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return UserMapper.INSTANCE.toDtoList(participants);
    }
//        return userMapper.toDto(eventParticipationRepository.findAllParticipantsByEventId(eventId));
//    }

    public Integer getParticipantCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}