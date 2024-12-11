package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.service.ProjectSubscriptionService;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectSubscriptionController {

    private final ProjectSubscriptionService projectSubscriptionService;

    @PostMapping("/{projectId}/subscribe")
    public String subscribeToProject(@RequestParam Long userId, @PathVariable Long projectId) {
        projectSubscriptionService.subscribeToProject(userId, projectId);
        return "You have successfully subscribed from the project.";
    }

    @DeleteMapping("/{projectId}/unsubscribe")
    public String unsubscribeFromProject(@RequestParam Long userId, @PathVariable Long projectId) {
        projectSubscriptionService.unsubscribeFromProject(userId, projectId);
        return "You have successfully unsubscribed from the project.";
    }
}
