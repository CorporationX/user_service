package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private static final long VALID_FOLLOWER_ID = 5L;
    private static final long VALID_FOLLOWEE_ID = 2L;

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionController subscriptionController;

    @Test
    void shouldReturnTrueWhenUnfollowIsSuccessful() {
        Map.Entry<String, Boolean> result = subscriptionController.unfollowUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID);

        assertEquals(
                Map.entry("isUnfollowed", true),
                result);

        verify(subscriptionService).unfollowUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID);
    }

    @Test
    void shouldReturnTrueWhenFollowIsSuccessful() {
        Map.Entry<String, Boolean> result = subscriptionController.followUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID);

        assertEquals(
                Map.entry("isFollowed", true),
                result);

        verify(subscriptionService).followUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID);
    }

}