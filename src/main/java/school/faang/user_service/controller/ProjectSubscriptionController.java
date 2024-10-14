package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.ProjectSubscriptionDto;
import school.faang.user_service.service.ProjectSubscriptionService;

@RestController
@RequestMapping("/project-subscriptions")
@RequiredArgsConstructor
@Validated
public class ProjectSubscriptionController {
    private final ProjectSubscriptionService projectSubscriptionService;

    @PostMapping("/subscribe")
    public ProjectSubscriptionDto subscribeToProject(
            @RequestParam @Min(1) long userId,
            @RequestParam @Min(1) long projectId) {
        return projectSubscriptionService.subscribeUserToProject(userId, projectId);
    }

}
