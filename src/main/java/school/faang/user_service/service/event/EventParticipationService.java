package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.EventParticipationServiceValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;
    private final UserMapper mapper;
    private final EventParticipationServiceValidator validator;

    public void registerParticipant(long eventId, long userId) {
        validator.validateEvent(eventId);
        validator.validateUserRegister(eventId, userId);

        repository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        validator.validateEvent(eventId);
        validator.validateUserUnregister(eventId, userId);

        repository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        validator.validateEvent(eventId);
        return mapper.toDtoList(repository.findAllParticipantsByEventId(eventId));
    }

    public Integer getParticipantsCount(long eventId) {
        validator.validateEvent(eventId);
        return repository.countParticipants(eventId);
    }
}
