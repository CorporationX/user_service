package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventParticipationValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final EventParticipationValidator eventParticipationValidator;

    @Transactional
    public void addParticipant(long eventId, long userId) {
        eventParticipationValidator.validateCanUserRegister(eventId, userId);
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void removeParticipant(long eventId, long userId) {
        eventParticipationValidator.validateCanUserUnregister(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        eventParticipationValidator.validateAreEventExist(eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public int getParticipantsCount(long eventId) {
        eventParticipationValidator.validateAreEventExist(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}
