package school.faang.user_service.service;

import school.faang.user_service.dto.ProjectDto;

public interface ProjectSubscriptionService {
    void followProject(long followerId, ProjectDto projectId);
    void unfollowProject(long followerId, ProjectDto projectId);
}
