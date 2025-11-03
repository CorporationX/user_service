package school.faang.user_service.service.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.SubscriptionRepository;

@RequiredArgsConstructor
@Component
public class SubscriptionSystemValidator {
    private final SubscriptionRepository subscriptionRepository;

    public void followValidation(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException("You can't follow yourself");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ForbiddenException("You already follow this user");
        }
    }

    public void unfollowValidation(long followerId, long followeeId) {
        if  (followerId == followeeId) {
            throw new ForbiddenException("You can't unfollow yourself");
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ForbiddenException("You don't follow this user");
        }
    }
}
