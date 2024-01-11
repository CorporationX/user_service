package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;

    public void validateSubscriptionId(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на самого себя");
        }
    }

    public void validateSubscriptionExits(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Такая подписка уже существует");
        }
    }

    public void validateUnsubscriptionId(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя отписаться от самого себя");
        }
    }

    public void validateUnsubscriptionExits(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Такoй подписки не существует");
        }
    }
}