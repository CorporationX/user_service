package school.faang.user_service.controller.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.profile.ProfileViewEvent;
import school.faang.user_service.publisher.profile.ProfileViewEventPublisher;
import school.faang.user_service.validator.profile.ViewProfileValidator;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileViewEventPublisher eventPublisher;
    private final ViewProfileValidator viewProfileValidator;
    private final UserContext userContext;

    @PostMapping("/view/{viewerId}")
    public void viewProfile(@PathVariable("viewerId") long viewerId) {
        long userId = userContext.getUserId();
        viewProfileValidator.validate(userId, viewerId);

        ProfileViewEvent event = new ProfileViewEvent(userId, viewerId, LocalDateTime.now());

        eventPublisher.publish(event);
    }
}
