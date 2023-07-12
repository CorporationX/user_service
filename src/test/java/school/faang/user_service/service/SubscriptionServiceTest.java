package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private final long followerId = 2;
    private final long followeeId = 1;

    @Test
    public void shouldAddNewFollowerById() {
        Mockito.doNothing().when(subscriptionRepository).followUser(followerId, followeeId);
        Assertions.assertDoesNotThrow(() -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldDeleteFollowerById() {
        Mockito.doNothing().when(subscriptionRepository).unfollowUser(followerId, followeeId);
        Assertions.assertDoesNotThrow(() -> subscriptionService.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(1)).unfollowUser(followerId, followeeId);
    }
}
