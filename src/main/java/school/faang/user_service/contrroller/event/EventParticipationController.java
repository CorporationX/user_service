package school.faang.user_service.contrroller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RegisterParticipantDto;
import school.faang.user_service.service.event.EventParticipationService;

@RestController("/Participation")
@Component
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;


    public void registerParticipant(RegisterParticipantDto registerParticipantDto) {
        long userId = registerParticipantDto.getUserId();
        long eventId = registerParticipantDto.getEventId();
        eventParticipationService.registerParticipant(userId, eventId);
    }
}
