package school.faang.user_service.service;

import school.faang.user_service.model.dto.ProjectDto;

public interface ProjectSubscriptionService {
    void followProject(long followerId, ProjectDto projectId);
}
