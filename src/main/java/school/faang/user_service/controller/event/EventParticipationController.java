package school.faang.user_service.controller.event;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GetParticipantsRequest;
import school.faang.user_service.dto.RegisterRequest;
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

    @PostMapping(path = "/register")
    public void registerParticipant(@RequestBody RegisterRequest request) {
        eventParticipationService.registerParticipant(request.getEventId(), request.getUserId());
    }

    @PostMapping(path = "/unregister")
    public void unregisterParticipant(@RequestBody RegisterRequest request) {
        eventParticipationService.unregisterParticipant(request.getEventId(), request.getUserId());
    }

    @PostMapping(path = "/getParticipants")
    public List<UserDto> getParticipants(@RequestBody GetParticipantsRequest request) {
        List<User> users = eventParticipationService.getParticipants(request.getEventId());
        return userMapper.toDto(users);
    }
}
