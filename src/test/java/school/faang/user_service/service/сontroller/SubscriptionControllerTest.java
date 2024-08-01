package school.faang.user_service.service.сontroller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionDto;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    public void testFollowUserWithSameId() throws DataValidationException {
        ResponseEntity<String> response = subscriptionController.followUser(1L, 1L);
        assertEquals("You cannot follow yourself!", response.getBody());
        assertEquals(400, response.getStatusCodeValue());
        verify(subscriptionService, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void testFollowUserWithDifferentId() throws DataValidationException {
        when(subscriptionService.followUser(1L, 2L)).thenReturn(true);

        ResponseEntity<String> response = subscriptionController.followUser(1L, 2L);
        assertEquals("Followed successfully", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(subscriptionService, times(1)).followUser(1L, 2L);
    }

    @Test
    public void testUnfollowUserWithSameId() {
        ResponseEntity<String> response = subscriptionController.unfollowUser(1L, 1L);
        assertEquals("You cannot unfollow yourself!", response.getBody());
        assertEquals(400, response.getStatusCodeValue());
        verify(subscriptionService, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    public void testUnfollowUserWithDifferentId() {
        when(subscriptionService.unfollowUser(1L, 2L)).thenReturn(true);

        ResponseEntity<String> response = subscriptionController.unfollowUser(1L, 2L);
        assertEquals("Unfollowed successfully", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(subscriptionService, times(1)).unfollowUser(1L, 2L);    }

    @Test
    public void testGetFollowers() {
        long followeeId = 1L;
        UserSubscriptionFilterDto filter = new UserSubscriptionFilterDto();
        List<UserSubscriptionDto> expected = List.of(new UserSubscriptionDto());

        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(expected);

        ResponseEntity<List<UserSubscriptionDto>> response = subscriptionController.getFollowers(followeeId, filter);

        assertEquals(expected, response.getBody());
        verify(subscriptionService).getFollowers(followeeId, filter);
    }

    @Test
    public void testGetFollowersCount() {
        long followerId = 1L;
        long expectedCount = 10;

        when(subscriptionService.getFollowersCount(followerId)).thenReturn(expectedCount);

        ResponseEntity<Long> response = subscriptionController.getFollowersCount(followerId);

        assertEquals(expectedCount, response.getBody());
        verify(subscriptionService).getFollowersCount(followerId);
    }

    @Test
    public void testGetFollowing() {
        long followeeId = 1L;
        UserSubscriptionFilterDto filter = new UserSubscriptionFilterDto();
        List<UserSubscriptionDto> expected = List.of(new UserSubscriptionDto());

        when(subscriptionService.getFollowing(followeeId, filter)).thenReturn(expected);

        ResponseEntity<List<UserSubscriptionDto>> response = subscriptionController.getFollowing(followeeId, filter);

        assertEquals(expected, response.getBody());
        verify(subscriptionService).getFollowing(followeeId, filter);
    }

    @Test
    public void testGetFollowingCount() {
        long followerId = 1L;
        long expectedCount = 20;

        when(subscriptionService.getFollowingCount(followerId)).thenReturn(expectedCount);

        ResponseEntity<Long> response = subscriptionController.getFollowingCount(followerId);

        assertEquals(expectedCount, response.getBody());
        verify(subscriptionService).getFollowingCount(followerId);
    }
}
