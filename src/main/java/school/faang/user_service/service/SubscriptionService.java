package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {
        boolean isAlreadyFollow = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isAlreadyFollow) {
            throw new DataValidationException("Following is already exists");
        }
        // нужно ли проверять существуют ли вообще такие пользователи? типо userRepository.existsById

        subscriptionRepository.followUser(followerId, followeeId);
    }
}
