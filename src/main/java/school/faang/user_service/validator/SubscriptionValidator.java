package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;

    public void checkIdIsGreaterThanZero(long Id) {
        if (Id <= 0) {
            throw new DataValidationException("Id пользователей должен быть больше нуля");
        }
    }

    public void checkFollowerAndFolloweeAreDifferent(long followerId, long followeeId) {
        checkIdIsGreaterThanZero(followerId);
        checkIdIsGreaterThanZero(followeeId);
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на себя или отписаться от себя");
        }
    }

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
}
