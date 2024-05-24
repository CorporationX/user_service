package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;

    public void checkSubscriptionExists(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует");
        }
    }

    public void checkSubscriptionNotExists(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписки не существует");
        }
    }

    public void checkFollowerAndFolloweeAreDifferent(Long followerId, Long followeeId) {
        if (followerId != null && followerId.equals(followeeId)) {
            throw new DataValidationException("Нельзя подписаться на себя или отписаться от себя");
        }
    }
}
