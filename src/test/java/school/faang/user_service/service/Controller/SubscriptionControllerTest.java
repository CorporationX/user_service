package school.faang.user_service.service.Controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.dto.FollowRequest;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;
    private static final int EXPECTED = 5;
    private static final String NAME_PATTERN = "John";

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    public void testFollowUser() {
        FollowRequest request = new FollowRequest(FOLLOWER_ID, FOLLOWEE_ID);

        assertNotNull(request);
        subscriptionController.followUser(request);

        verify(subscriptionService).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testunFollowUser() {
        FollowRequest request = new FollowRequest(FOLLOWER_ID, FOLLOWEE_ID);

        assertNotNull(request);
        subscriptionController.unfollowUser(request);

        verify(subscriptionService).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testGetFollowers() {
        UserFilterDto filter = new UserFilterDto(NAME_PATTERN, null,
                null, null);
        List<UserDto> expected = List.of(new UserDto(FOLLOWER_ID, NAME_PATTERN, null));

        when(subscriptionService.getFollowers(FOLLOWER_ID, filter)).thenReturn(expected);

        List<UserDto> result = subscriptionController.getFollowers(FOLLOWER_ID, filter);

        assertNotNull(result);
        assertEquals(expected, result);

        verify(subscriptionService).getFollowers(FOLLOWER_ID, filter);
    }

    @Test
    public void testGetFollowersCount() {
        when(subscriptionService.getFollowersCount(FOLLOWER_ID)).thenReturn(5);

        int result = subscriptionController.getFollowersCount(FOLLOWER_ID);

        assertNotNull(result);
        assertEquals(EXPECTED, result);

        verify(subscriptionService).getFollowersCount(FOLLOWER_ID);
    }

    @Test
    public void testGetFollowing() {
        UserFilterDto filter = new UserFilterDto("Alice", null,
                null, null);
        List<UserDto> expected = List.of(new UserDto(FOLLOWER_ID, "Alice", null));

        when(subscriptionService.getFollowing(FOLLOWER_ID, filter)).thenReturn(expected);

        List<UserDto> result = subscriptionController.getFollowing(FOLLOWER_ID, filter);

        assertNotNull(result);
        assertEquals(expected, result);

        verify(subscriptionService).getFollowing(FOLLOWER_ID, filter);
    }

    @Test
    public void testGetFollowingCount() {
        when(subscriptionService.getFollowingCount(FOLLOWER_ID)).thenReturn(EXPECTED);

        int result = subscriptionController.getFollowingCount(FOLLOWER_ID);

        assertNotNull(result);
        assertEquals(EXPECTED, result);

        verify(subscriptionService).getFollowingCount(FOLLOWER_ID);
    }
}
