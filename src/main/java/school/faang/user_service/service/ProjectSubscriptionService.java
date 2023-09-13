package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.ProjectServiceClient;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.dto.redis.ProjectFollowerEventDto;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;

@Service
@RequiredArgsConstructor
public class ProjectSubscriptionService {
    private final ProjectSubscriptionRepository projectSubscriptionRepository;
    private final ProjectServiceClient projectServiceClient;
    private final ProjectFollowerEventPublisher projectFollowerEventPublisher;

    @Transactional
    public void followProject(long followerId, long projectId) {
        long ownerId = validate(followerId, projectId).getOwnerId();
        projectSubscriptionRepository.followProject(followerId, projectId);
        ProjectFollowerEventDto projectFollowerEventDto = new ProjectFollowerEventDto(followerId, ownerId, projectId);
        projectFollowerEventPublisher.publishMessage(projectFollowerEventDto);
    }

    private ProjectDto validate(long followerId, long projectId) {
        ProjectDto projectDto = projectServiceClient.getProject(projectId);
        if (projectSubscriptionRepository.existsByFollowerIdAndProjectId(followerId, projectId)) {
            throw new DataValidException("Subscription already exists");
        }
        if (projectDto.getOwnerId() == followerId) {
            throw new DataValidException("The user cannot subscribe or unsubscribe from his project");
        }
        return projectDto;
    }
}
