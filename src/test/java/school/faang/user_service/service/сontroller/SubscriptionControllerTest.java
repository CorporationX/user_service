package school.faang.user_service.service.сontroller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(1L, 1L));
        verify(subscriptionService, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void testFollowUserWithDifferentId() throws DataValidationException {
        subscriptionController.followUser(1L, 2L);

        verify(subscriptionService, times(1)).followUser(1L, 2L);
    }

    @Test
    public void testUnfollowUserWithSameId() {
        assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(1L, 1L));
        verify(subscriptionService, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    public void testUnfollowUserWithDifferentId() throws DataValidationException {
        subscriptionController.unfollowUser(1L, 2L);
        verify(subscriptionService, times(1)).unfollowUser(1L, 2L);
    }

    @Test
    public void testGetFollowers() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> expected = List.of(new UserDto());

        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(expected);

        List<UserDto> actual = subscriptionController.getFollowers(followeeId, filter);

        assertEquals(expected, actual);
        verify(subscriptionService).getFollowers(followeeId, filter);
    }

    @Test
    public void testGetFollowersCount() {
        long followerId = 1L;
        long expectedCount = 10;

        when(subscriptionService.getFollowersCount(followerId)).thenReturn(expectedCount);

        long actualCount = subscriptionController.getFollowersCount(followerId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionService).getFollowersCount(followerId);
    }

    @Test
    public void testGetFollowing() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> expected = List.of(new UserDto());

        when(subscriptionService.getFollowing(followeeId, filter)).thenReturn(expected);

        List<UserDto> actual = subscriptionController.getFollowing(followeeId, filter);

        assertEquals(expected, actual);
        verify(subscriptionService).getFollowing(followeeId, filter);
    }

    @Test
    public void testGetFollowingCount() {
        long followerId = 1L;
        long expectedCount = 20;

        when(subscriptionService.getFollowingCount(followerId)).thenReturn(expectedCount);

        long actualCount = subscriptionController.getFollowingCount(followerId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionService).getFollowingCount(followerId);
    }
}
