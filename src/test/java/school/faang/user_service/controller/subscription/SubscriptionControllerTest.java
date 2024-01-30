package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionController subscriptionController;


    @Test
    void shouldReturnTrueWhenUnfollowIsSuccessful() {
        long followerId = 5L;
        long followeeId = 2L;

        Map.Entry<String, Boolean> result = subscriptionController.unfollowUser(followerId, followeeId);

        assertEquals(
                Map.entry("isUnfollowed", true),
                result);

        verify(subscriptionService).unfollowUser(followerId, followeeId);
    }

    @Test
    void shouldReturnTrueWhenFollowIsSuccessful() {
        long followerId = 5L;
        long followeeId = 2L;

        Map.Entry<String, Boolean> result = subscriptionController.followUser(followerId, followeeId);

        assertEquals(
                Map.entry("isFollowed", true),
                result);

        verify(subscriptionService).followUser(followerId, followeeId);
    }

}