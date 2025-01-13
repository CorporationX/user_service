package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import school.faang.user_service.dto.event.participant.RegisterParticipantDto;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final Map<Long, Long> particaptionDtoMap = new HashMap<>();

    @Transactional
    public void registerParticipant(RegisterParticipantDto registerParticipantDto) {
        long eventId = registerParticipantDto.getEventId();
        long userId = registerParticipantDto.getUserId();

        particaptionDtoMap.put(eventId, userId);
        eventParticipationRepository.register(particaptionDtoMap.get(eventId), userId);
    }

    @Transactional
    EventParticipationService eventParticipationService() {
        return new EventParticipationService(eventParticipationRepository);
    }


}
