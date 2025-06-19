package school.faang.user_service.service;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.ParticipantMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final ParticipantMapper participantMapper;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (isParticipantRegistered(eventId, userId)) {
            throw new DuplicateRequestException("User is already registered for this event");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        if (isParticipantRegistered(eventId, userId)) {
            throw new IllegalStateException("User is not registered for this event");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .map(participantMapper::toDto)
                .toList();
    }

    public Integer getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isParticipantRegistered(long eventId, long userId) {
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }
}
