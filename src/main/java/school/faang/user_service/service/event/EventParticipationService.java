package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventRegistrationNotificationDto;
import school.faang.user_service.dto.subscribe.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.ParticipantRegistrationException;
import school.faang.user_service.mapper.UserDTOMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.publisher.EventRegistrationEventPublisher;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationService {
    private final UserRepository userRepository;
    private final EventParticipationRepository repository;
    private final EventRegistrationEventPublisher eventRegistrationEventPublisher;
    private final UserDTOMapper mapper;

    @Value("${application.publisher-messages.event-registration.notification.telegram}")
    private String eventRegistrationMessage;

    @Transactional
    public void register(Long eventId, Long userId){
        validateIdsNotNull(eventId, userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id " + userId + " not found")
        );

        boolean alreadyRegistered = repository.existsByEventIdAndUserId(eventId, userId);

        if (alreadyRegistered) {
            log.error("User already registered with id: {}", userId);
            throw new ParticipantRegistrationException("User already registered");
        }

        repository.register(eventId, userId);

        EventRegistrationNotificationDto notificationDto = EventRegistrationNotificationDto.builder()
                .userId(String.valueOf(userId))
                .eventId(String.valueOf(eventId))
                .message(eventRegistrationMessage)
                .telegramId(user.getTelegramId())
                .build();
        eventRegistrationEventPublisher.publish(notificationDto);
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
