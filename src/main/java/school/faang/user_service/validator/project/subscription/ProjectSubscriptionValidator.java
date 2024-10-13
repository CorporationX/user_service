package school.faang.user_service.validator.project.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.ProjectSubscriptionRepository;

@Component
@RequiredArgsConstructor
public class ProjectSubscriptionValidator {

    private final ProjectSubscriptionRepository repository;

    public boolean isAlreadySubscribed(long followerId, long projectId) {
        return repository.existsByFollowerIdAndProjectId(followerId, projectId);
    }
}
