package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private long validFollowerId;
    private long validFolloweeId;

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionController subscriptionController;

    @BeforeEach
    void init() {
        validFollowerId = 5L;
        validFolloweeId = 2L;
    }

    @Test
    void shouldReturnTrueWhenUnfollowIsSuccessful() {
        Map.Entry<String, Boolean> result = subscriptionController.unfollowUser(validFollowerId, validFolloweeId);

        assertEquals(
                Map.entry("isUnfollowed", true),
                result);

        verify(subscriptionService).unfollowUser(validFollowerId, validFolloweeId);
    }

    @Test
    void shouldReturnTrueWhenFollowIsSuccessful() {
        Map.Entry<String, Boolean> result = subscriptionController.followUser(validFollowerId, validFolloweeId);

        assertEquals(
                Map.entry("isFollowed", true),
                result);

        verify(subscriptionService).followUser(validFollowerId, validFolloweeId);
    }

    @Test
    void shouldReturnFollowingCountWhenFollowerIdIsValid() {
        when(subscriptionService.getFollowingCount(validFollowerId))
                .thenReturn(5);

        Map.Entry<String, Integer> result = subscriptionController.getFollowingCount(validFollowerId);

        assertEquals(
                Map.entry("followingCount", 5),
                result);

        verify(subscriptionService).getFollowingCount(validFollowerId);
    }

    @Test
    void shouldReturnFollowersCountWhenFolloweeIdIsValid() {
        when(subscriptionService.getFollowersCount(validFolloweeId))
                .thenReturn(5);

        Map.Entry<String, Integer> result = subscriptionController.getFollowersCount(validFolloweeId);

        assertEquals(
                Map.entry("followersCount", 5),
                result);

        verify(subscriptionService).getFollowersCount(validFolloweeId);
    }


}