package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.SubscriptionService;

@Service
@RequiredArgsConstructor
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public void validateUserIds(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("FollowerId и followeeId не могут совпадать");
        }
    }

    public boolean validateSubscription(long followerId, long followeeId) {
        if (!userRepository.existsById(followerId) || !userRepository.existsById(followeeId)) {
            throw new DataValidationException("Нет пользователя с таким айди");
        }

        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}