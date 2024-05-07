package school.faang.user_service.controller.event;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Data
@Controller
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @Autowired
    public EventParticipationController(EventParticipationService eventParticipationService) {
        this.eventParticipationService = eventParticipationService;
    }

    public void registerParticipant(long eventId, UserDto userDto) {
        eventParticipationService.registerParticipant(eventId, userDto.getId());
    }

    @DeleteMapping("/events/{eventId}/participants/{userId}")
    public void unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/events/{eventId}/participants")
    public List<User> getParticipants(@PathVariable long eventId) {
        return eventParticipationService.getAllParticipantsByEventId(eventId);
    }

    @GetMapping("/{eventId}/participants/count")
    public int getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
