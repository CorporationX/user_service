package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import school.faang.user_service.model.dto.ProjectSubscriptionDto;

public interface ProjectSubscriptionService {
    @Transactional
    ProjectSubscriptionDto subscribeUserToProject(long userId, long projectId);
}
