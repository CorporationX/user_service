package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.eventService.EventParticipationServiceImpl;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationServiceImpl eventParticipationServiceImpl;

    public void registerParticipant(@Valid UserDto userDTO, @Valid EventDto eventDto) {
        eventParticipationServiceImpl.registerParticipant(userDTO.getId(), eventDto.getId());
    }

    public void unregisterParticipant(@Valid UserDto userDTO, @Valid EventDto eventDto) {
        eventParticipationServiceImpl.unregisterParticipant(userDTO.getId(), eventDto.getId());
    }

    public void getParticipant(@Valid EventDto eventDto) {
        List<UserDto> eventUsers = eventParticipationServiceImpl.getParticipant(eventDto.getId());
    }

    public void getParticipantsCount(@Valid EventDto eventDto) {
        int countEventUsers = eventParticipationServiceImpl.getParticipantsCount(eventDto.getId());
    }

}
