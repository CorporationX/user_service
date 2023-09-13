package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.ProjectSubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions/project")
public class ProjectSubscriptionController {
    private final ProjectSubscriptionService projectSubscriptionService;

    @PostMapping("/follow/{followerId}/{projectId}")
    public void followProject(@PathVariable long followerId, @PathVariable long projectId) {
        projectSubscriptionService.followProject(followerId, projectId);
    }
}
