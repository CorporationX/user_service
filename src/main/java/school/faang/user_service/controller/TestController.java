package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.event.ProfilePicEvent;
import school.faang.user_service.publisher.ProfilePicEventPublisher;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController { //todo: delete

    private final UserContext userContext;
    private final ProfilePicEventPublisher publisher;

    @PostMapping("/event/profile-pic")
    public String sendProfilePicEventToRedis() {

        ProfilePicEvent profilePicEvent = ProfilePicEvent.builder()
                .userId(userContext.getUserId())
                .avatarUrl("some avatar url")
                .build();

        publisher.publish(profilePicEvent);

        return "sent";
    }
}
