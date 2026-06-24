package school.faang.user_service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserEventCountResDto;
import school.faang.user_service.dto.UserResDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@Transactional
public class EventParticipationService {

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(Long eventId, Long userId) {
        if(eventId == null) {
            throw new IllegalArgumentException("its null");
        }
        List<User> allParticipants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (allParticipants.stream().anyMatch(user -> user.getId() == userId)) {
            throw new IllegalArgumentException("Пользователь уже зарегистрирован на это событие.");
        }

        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        List<User> allParticipants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (allParticipants.stream().noneMatch(user -> user.getId() == userId)) {
            throw new IllegalArgumentException("Пользователь не зарегистрирован на это событие.");
        }

        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserResDto> getParticipant(Long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .map(user -> new UserResDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }

    public UserEventCountResDto getParticipantCount(Long eventId) {
        int countParticipants = eventParticipationRepository.countParticipants(eventId);
        return UserEventCountResDto
                .builder()
                .count(countParticipants)
                .build();
    }
}