package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.ProjectServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.ProjectDto;
import school.faang.user_service.model.dto.ProjectSubscriptionDto;
import school.faang.user_service.model.entity.ProjectSubscription;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.ProjectSubscriptionMapper;
import school.faang.user_service.model.event.ProjectFollowerEvent;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.ProjectSubscriptionService;
import school.faang.user_service.validator.ProjectSubscriptionValidator;

@Service
@RequiredArgsConstructor
public class ProjectSubscriptionServiceImpl implements ProjectSubscriptionService {
    private final ProjectSubscriptionRepository projectSubscriptionRepository;
    private final UserContext userContext;
    private final ProjectSubscriptionMapper projectSubscriptionMapper;
    private final ProjectSubscriptionValidator projectSubscriptionValidator;
    private final ProjectServiceClient projectServiceClient;
    private final ProjectFollowerEventPublisher projectFollowerEventPublisher;

    @Override
    @Transactional
    public ProjectSubscriptionDto subscribeUserToProject(long userId, long projectId) {
        long contextUserId = userContext.getUserId();
        projectSubscriptionValidator.validateUser(contextUserId, userId);

        if (projectSubscriptionRepository.existsByFollowerIdAndProjectId(userId, projectId)) {
            throw new DataValidationException(String.format("Project subscription of userId = %d on " +
                    "projectId = %d already exists", userId, projectId));
        }

        projectSubscriptionRepository.followProject(userId, projectId);
        ProjectDto projectDto = projectServiceClient.getProject(projectId);
        ProjectFollowerEvent projectFollowerEvent = new ProjectFollowerEvent();
        projectFollowerEvent.setFollowerId(userId);
        projectFollowerEvent.setProjectId(projectId);
        projectFollowerEvent.setCreatorId(projectDto.getOwnerId());
        projectFollowerEventPublisher.publish(projectFollowerEvent);

        ProjectSubscription projectSubscription = projectSubscriptionRepository.findByUserIdAndProjectId(userId, projectId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Project subscription of userId = %d on " +
                        "projectId = %d not found", userId, projectId)));

        return projectSubscriptionMapper.toDto(projectSubscription);
    }
}
