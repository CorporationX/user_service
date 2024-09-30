package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.EventParticipationServiceValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository repository;
    private final UserMapper mapper;
    private final EventParticipationServiceValidator validator;

    @Override
    public void registerParticipant(long eventId, long userId) {
        validator.validateUserRegister(repository.findAllParticipantsByEventId(eventId), userId);

        repository.register(eventId, userId);
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        validator.validateUserUnregister(repository.findAllParticipantsByEventId(eventId), userId);

        repository.unregister(eventId, userId);
    }

    @Override
    public List<UserDto> getParticipant(long eventId) {
        return mapper.toDtoList(repository.findAllParticipantsByEventId(eventId));
    }

    @Override
    public Integer getParticipantsCount(long eventId) {
        return repository.countParticipants(eventId);
    }
}
