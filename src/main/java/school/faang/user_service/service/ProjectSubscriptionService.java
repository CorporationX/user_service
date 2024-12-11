package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.ProjectFollowerEventDto;
import school.faang.user_service.entity.Project;
import school.faang.user_service.entity.ProjectSubscription;
import school.faang.user_service.entity.User;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;
import school.faang.user_service.publisher.ProjectUnfollowEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.repository.ProjectRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectSubscriptionService {

    private final ProjectSubscriptionRepository projectSubscriptionRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectFollowerEventPublisher projectFollowerEventPublisher;
    private final ProjectUnfollowEventPublisher projectUnfollowEventPublisher;

    public void subscribeToProject(Long userId, Long projectId) {
        if (projectSubscriptionRepository.existsByFollowerIdAndProjectId(userId, projectId)) {
            throw new IllegalStateException("Пользователь уже подписан на этот проект");
        }

        User follower = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));

        ProjectSubscription subscription = ProjectSubscription.builder()
            .follower(follower)
            .projectId(projectId)
            .build();

        projectSubscriptionRepository.save(subscription);
        log.info("Пользователь с ID {} подписался на проект с ID {}", userId, projectId);

        Long creatorId = getCreatorId(projectId);

        ProjectFollowerEventDto event = new ProjectFollowerEventDto(follower.getId(), projectId, creatorId);
        projectFollowerEventPublisher.publish(event);
    }

    public void unsubscribeFromProject(Long userId, Long projectId) {
        User follower = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));

        ProjectSubscription subscription = projectSubscriptionRepository.findByFollowerIdAndProjectId(userId, projectId)
            .orElseThrow(() -> new IllegalArgumentException("Подписка не найдена"));

        projectSubscriptionRepository.delete(subscription);
        log.info("Пользователь с ID {} отписался от проекта с ID {}", userId, projectId);

        Long creatorId = getCreatorId(projectId);

        ProjectFollowerEventDto event = new ProjectFollowerEventDto(follower.getId(), projectId, creatorId);
        projectUnfollowEventPublisher.publishUnfollow(event);
    }


    private Long getCreatorId(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Проект с ID " + projectId + " не найден"));
        return project.getCreatorId();
    }
}
