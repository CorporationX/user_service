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

    @Test
    public void shouldAddNewFollowerById() {
        long followerId = 2;
        long followeeId = 1;

        Mockito.doNothing().when(subscriptionRepository).followUser(followerId, followeeId);
        Assertions.assertDoesNotThrow(() -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(followerId, followeeId);
    }
}
