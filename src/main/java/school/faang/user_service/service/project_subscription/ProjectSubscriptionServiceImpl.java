package school.faang.user_service.service.project_subscription;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.ProjectServiceClient;
import school.faang.user_service.dto.event.FollowerEvent;
import school.faang.user_service.dto.event.ProjectFollowerEvent;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.validator.ProjectSubscriptionValidator;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProjectSubscriptionServiceImpl implements ProjectSubscriptionService {
    private final ProjectSubscriptionRepository projectSubscriptionRepository;
    private final FollowerEventPublisher followerEventPublisher;
    private final ProjectFollowerEventPublisher projectFollowerEventPublisher;
    private final ProjectSubscriptionValidator projectSubscriptionValidator;
    private final ProjectServiceClient projectServiceClient;

    @Override
    @Transactional
    public void followProject(Long followerId, Long projectId) {
        if (existsByFollowerIdAndProjectId(followerId, projectId)) {
            throw new DataValidationException(String.format("пользователь с id= %d уже подписан на проект с id= %d", followerId, projectId));
        }
        ProjectFollowerEvent projectFollowerEvent = generateFollowerEvent(followerId, projectId);
        projectSubscriptionValidator.validateProjectSubscription(followerId);
        projectSubscriptionRepository.followProject(followerId, projectId);
        FollowerEvent followerEvent = FollowerEvent.builder()
                .followerId(followerId)
                .projectId(projectId)
                .followingDate(LocalDateTime.now().withNano(0))
                .build();
        followerEventPublisher.publish(followerEvent);
        projectFollowerEventPublisher.publish(projectFollowerEvent);
    }

    private boolean existsByFollowerIdAndProjectId(Long followerId, Long projectId) {
        return projectSubscriptionRepository.existsByFollowerIdAndProjectId(followerId, projectId);
    }

    private ProjectFollowerEvent generateFollowerEvent(Long followerId, Long projectId) {
        ProjectDto projectDto = projectServiceClient.getProjectById(projectId);
        if (projectDto == null) {
            throw new EntityNotFoundException(String.format("project with id = %d mot found", projectId));
        }
        return ProjectFollowerEvent
                .builder()
                .ownerId(projectDto.getOwnerId())
                .projectId(projectId)
                .followerId(followerId)
                .build();
    }
}