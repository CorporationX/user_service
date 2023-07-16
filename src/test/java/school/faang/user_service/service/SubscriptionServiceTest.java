package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SubscriptionServiceTest{
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetFollowingCount() {
        long followerId = 123;
        int expectedCount = 10;
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectedCount);

        int result = subscriptionService.getFollowingCount(followerId);

        assertEquals(expectedCount, result);
    }
}
