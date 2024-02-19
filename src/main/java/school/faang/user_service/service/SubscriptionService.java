package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.SubscriptionDto;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final FollowerEventPublisher followerEventPublisher;

    public void followUser(SubscriptionDto subscriptionDto) {
        long followerId = subscriptionDto.getFollowerId();
        long followeeId = subscriptionDto.getFolloweeId();

        validationSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        followerEventPublisher.publish(new FollowerEvent(followerId, followeeId, LocalDateTime.now()));
    }

    private void validationSubscription(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("Non-existent user id");
        }

        if (followerId == followeeId) {
            throw new IllegalArgumentException("You can not subscribe to yourself");
        }
    }
}
