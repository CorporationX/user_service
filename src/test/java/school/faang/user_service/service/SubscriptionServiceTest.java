package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void followUser_ShouldCallValidatorAndRepositoryMethod() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionValidator, times(1)).validateSubscription(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void unfollowUser_ShouldCallValidatorAndRepositoryMethod() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionValidator, times(1)).validateUnsubscription(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }
}

