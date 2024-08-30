package school.faang.user_service.service.project.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.event.ProjectFollowerEvent;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;

@Service
@RequiredArgsConstructor
public class ProjectSubscriptionService {

    private final ProjectFollowerEventPublisher projectFollowerEventPublisher;

    public void followProject(Long id, @NotNull ProjectDto projectDto) {
        ProjectFollowerEvent projectFollowerEvent = new ProjectFollowerEvent(projectDto.getProjectId(),id,projectDto.getOwnerId());
        projectFollowerEventPublisher.sendEvent(projectFollowerEvent);
    }
}
