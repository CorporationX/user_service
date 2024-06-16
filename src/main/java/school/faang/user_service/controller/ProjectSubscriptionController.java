package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.project_subscription.ProjectSubscriptionService;

@RestController
@RequestMapping("/api/v1/user/project")
@AllArgsConstructor
public class ProjectSubscriptionController {
    private final ProjectSubscriptionService projectSubscriptionService;

    @PostMapping("/subscribe")
    public void followProject(@RequestParam(name = "followerId") Long followerId,
                           @RequestParam(name = "projectId") Long projectId) {
        projectSubscriptionService.followProject(followerId, projectId);
    }
}