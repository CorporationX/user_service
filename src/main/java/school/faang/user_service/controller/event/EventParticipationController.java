package school.faang.user_service.controller.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mappers.UserMapper;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;
    private final UserMapper userMapper;

    @PostMapping(path = "/{eventId}/register/{userId}")
    public void registerParticipant(@PathVariable("eventId") long eventId, @PathVariable("userId") long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping(path = "/{eventId}/unregister/{userId}")
    public void unregisterParticipant(@PathVariable("eventId") long eventId, @PathVariable("userId") long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping(path = "/{eventId}/getParticipants")
    public List<UserDto> getParticipants(@PathVariable("eventId") long eventId) {
        List<User> users = eventParticipationService.getParticipants(eventId);
        return userMapper.toDto(users);
    }

    @GetMapping(path = "/{eventId}/getParticipantsCount")
    public int getParticipantsCount(@PathVariable("eventId") long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
