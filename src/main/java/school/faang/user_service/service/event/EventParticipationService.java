package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscribe.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ParticipantRegistrationException;
import school.faang.user_service.mapper.user.UserDTOMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationService {
    private final EventParticipationRepository repository;
    private final UserDTOMapper mapper;

    public void register(Long eventId, Long userId){
        validateIdsNotNull(eventId, userId);

        boolean alreadyRegistered = repository.existsByEventIdAndUserId(eventId, userId);

        if (alreadyRegistered) {
            log.error("User already registered with id: {}", userId);
            throw new ParticipantRegistrationException("User already registered");
        }

        repository.register(eventId, userId);
    }

    public void unregister(Long eventId, Long userId){
        validateIdsNotNull(eventId, userId);

        boolean alreadyRegistered = repository.existsByEventIdAndUserId(eventId, userId);

        if (!alreadyRegistered) {
            log.error("User isn't registered: {}", userId);
            throw new ParticipantRegistrationException("User isn't registered");
        }

        repository.unregister(eventId, userId);
    }

    public List<UserDTO> findAllParticipantsByEventId(Long eventId){
        validateIdsNotNull(eventId);

        List<User> users = repository.findAllParticipantsByEventId(eventId);

        return mapper.toDTO(users);
    }

    public int countParticipants(Long eventId){
        validateIdsNotNull(eventId);

        return repository.countParticipants(eventId);

    }

    private void validateIdsNotNull(Long...ids) {
        for (Long id : ids){
            if (id == null){
                log.error("Id is null");
                throw new IllegalArgumentException("Id must not be null");
            }
        }
    }
}
