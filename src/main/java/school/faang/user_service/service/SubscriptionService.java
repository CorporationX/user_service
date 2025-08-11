package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowerEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.utils.Utils;
import school.faang.user_service.validator.user.SubscriptionValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    public static final String FOLLOWEE_NOT_FOUND = "Followee not found. Id is: {}";

    private final FollowerEventPublisher followerEventPublisher;
    private final UserValidator userValidator;
    private final SubscriptionValidator subscriptionValidator;
    private final SubscriptionRepository subscriptionRepository;
    private final Utils utils;

    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        userValidator.validate(followerId);
        userValidator.validate(followeeId);
        subscriptionValidator.validate(new SubscriptionValidator.SubscriptionValidationData(followerId, followeeId));

        FollowerEventDto event = new FollowerEventDto(followerId, followeeId, LocalDateTime.now());
        followerEventPublisher.publish(event);
    }

    @Transactional(readOnly = true)
    public Set<Long> findFollowerIdsByFolloweeId(Long followeeId) {
        User followee = subscriptionRepository.findById(followeeId)
            .orElseThrow(() -> new UserNotFoundException(utils.format(FOLLOWEE_NOT_FOUND, followeeId)));
        return subscriptionRepository.findFollowerIdByFolloweeId(followee.getId());
    }
}