package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;


    @Test
    void testGetFollowersCount() {
        long followeeId = 123;
        int followersCount = 42;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(followersCount);

        int result = subscriptionService.getFollowersCount(followeeId);

        assertEquals(followersCount, result);
    }
}