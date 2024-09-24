package school.faang.user_service.controller.event;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CountDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event-participations")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;
    private final RecommendationRequestMapper.UserMapper userMapper;

    @PutMapping("/{eventId}/register")
    public void registerParticipant(@PathVariable("eventId") long eventId, @RequestBody UserDto userDto) {
        eventParticipationService.registerParticipant(eventId, userDto.getId());
    }

    @PutMapping("/{eventId}/unregister")
    public void unregisterParticipant(@PathVariable("eventId") long eventId, @RequestBody UserDto userDto) {
        eventParticipationService.unregisterParticipant(eventId, userDto.getId());
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(@PathVariable("eventId") long eventId) {
        List<User> users = eventParticipationService.getParticipants(eventId);
        return userMapper.toDto(users);
    }

    @GetMapping("/{eventId}/participants-count")
    public CountDto getParticipantsCount(@PathVariable("eventId") long eventId) {
        int count = eventParticipationService.getParticipantsCount(eventId);
        return CountDto.builder().eventId(eventId).participantsCount(count).build();
    }
}
