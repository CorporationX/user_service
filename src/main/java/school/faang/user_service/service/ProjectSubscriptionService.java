package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.ProjectSubscription;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectSubscriptionService {

    private final ProjectSubscriptionRepository projectSubscriptionRepository;
    private final UserRepository userRepository;

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

    }

    public void unsubscribeFromProject(Long userId, Long projectId) {
        ProjectSubscription subscription = projectSubscriptionRepository.findByFollowerIdAndProjectId(userId, projectId)
            .orElseThrow(() -> new IllegalArgumentException("Подписка не найдена"));

        projectSubscriptionRepository.delete(subscription);
        log.info("Пользователь с ID {} отписался от проекта с ID {}", userId, projectId);
    }
}
