package school.faang.user_service.service.impl.project.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.ProjectDto;
import school.faang.user_service.model.event.ProjectFollowerEvent;
import school.faang.user_service.publisher.ProjectSubscriptionPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.ProjectSubscriptionService;
import school.faang.user_service.validator.project.subscription.ProjectSubscriptionValidator;

@Service
@RequiredArgsConstructor
public class ProjectSubscriptionServiceImpl implements ProjectSubscriptionService {

    private final ProjectSubscriptionRepository subscriptionRepository;
    private final ProjectSubscriptionValidator validator;
    private final ProjectSubscriptionPublisher publisher;

    @Transactional
    public void followProject(long followerId, ProjectDto projectDto) {
        if (!validator.isAlreadySubscribed(followerId, projectDto.projectId())) {
            subscriptionRepository.followProject(followerId, projectDto.projectId());
            publisher.publish(new ProjectFollowerEvent(followerId, projectDto.projectId(), projectDto.ownerId()));
        }
    }

}
