package school.faang.user_service.controller.project.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.service.project.subscription.ProjectSubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class ProjectSubscriptionController {
    private final ProjectSubscriptionService projectSubscriptionService;

    @PostMapping("/{id}/project")
    public void followProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        projectSubscriptionService.followProject(id, projectDto);
    }
}
