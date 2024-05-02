package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validation.SubscriptionValidator;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    public final SubscriptionValidator validator;

    public void followUser(long followerId, long followeeId) {
        validator.validateIsExists(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }
}
