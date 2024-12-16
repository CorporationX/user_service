package school.faang.user_service.controller.subscription;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.ProjectDto;
import school.faang.user_service.service.ProjectSubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions/projects")
@Validated
public class ProjectSubscriptionController {

    private final ProjectSubscriptionService subscriptionService;

    @PostMapping("/follow/{userId}")
    public void followProject(@PathVariable @Positive long userId,
                              @RequestBody ProjectDto projectDto) {
        subscriptionService.followProject(userId, projectDto);
    }

    @DeleteMapping("/unfollow/{userId}")
    public void unfollowProject(@PathVariable @Positive long userId,
                                @RequestBody ProjectDto projectDto) {
        subscriptionService.unfollowProject(userId, projectDto);
    }
}
