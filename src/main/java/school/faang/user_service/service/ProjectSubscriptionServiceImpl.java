package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.ProjectDto;
import school.faang.user_service.dto.FollowerProjectEvent;
import school.faang.user_service.publisher.FollowerProjectPublisher;
import school.faang.user_service.publisher.UnfollowProjectPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectSubscriptionServiceImpl implements ProjectSubscriptionService {

    private final ProjectSubscriptionRepository subscriptionRepository;
    private final ProjectSubscriptionValidator validator;
    private final FollowerProjectPublisher followerProjectPublisher;
    private final UnfollowProjectPublisher unfollowProjectPublisher;

    @Transactional
    public void followProject(long followerId, ProjectDto projectDto) {
        if (validator.isAlreadySubscribed(followerId, projectDto.projectId())) {
            log.warn("Попытка подписки на проект {} пользователем {}. Подписка уже существует.",
                projectDto.projectId(), followerId);
            throw new IllegalStateException("Пользователь уже подписан на этот проект.");
        }

        subscriptionRepository.followProject(followerId, projectDto.projectId());
        log.info("Пользователь {} успешно подписался на проект {}.", followerId, projectDto.projectId());

        followerProjectPublisher.publishFollowerEvent(new FollowerProjectEvent(followerId, projectDto.projectId(), projectDto.ownerId(), LocalDateTime.now()));
        log.info("Событие подписки опубликовано для пользователя {} на проект {}.", projectDto.ownerId(), projectDto.projectId());
    }

    @Transactional
    public void unfollowProject(long followerId, ProjectDto projectDto) {
        if (!validator.isAlreadySubscribed(followerId, projectDto.projectId())) {
            log.warn("Попытка отписки от проекта {} пользователем {}. Подписка не найдена.",
                projectDto.projectId(), followerId);
            throw new IllegalStateException("Пользователь не подписан на этот проект.");
        }

        subscriptionRepository.unfollowProject(followerId, projectDto.projectId());
        log.info("Пользователь {} успешно отписался от проекта {}.", followerId, projectDto.projectId());

        unfollowProjectPublisher.publishUnfollowEvent(new FollowerProjectEvent(followerId, projectDto.projectId(), projectDto.ownerId(), LocalDateTime.now()));
        log.info("Событие отписки опубликовано для пользователя {} от проекта {}.", projectDto.ownerId(), projectDto.projectId());
    }
}
