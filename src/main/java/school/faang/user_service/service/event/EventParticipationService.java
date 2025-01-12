package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RegisterParticipantDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final Map<Long, Long> eventParticipationMap = new HashMap<>();
    private final EventParticipationRepository eventParticipationRepository;


    public void registerParticipant(long eventId, long userId) {
        if (eventParticipationMap.get(eventId) == null || eventParticipationMap.get(userId) == null) {
            throw new DataValidationException("ID не может быть пустым!");
        }
        eventParticipationMap.put(eventId, userId);
    }
}
