package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private UserFilterDto userFilterDto;

    @Mock
    private UserDto userDto;

    @InjectMocks
    private SubscriptionController subscriptionController;

    long followerId;
    long followeeId;
    int expectationCount;
    List<UserDto> expectationUsersDto;

    @BeforeEach
    public void setUp() {
        subscriptionController = new SubscriptionController(subscriptionService);
        expectationUsersDto = List.of(userDto);
        followerId = 1L;
        followeeId = 2L;
        expectationCount = 55;
    }

    @Test
    public void testIdenticalUserIdsAreInvalid() {
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followerId));
        assertThrows(DataValidationException.class, () -> subscriptionController.unfollowUser(followerId, followerId));
    }

    @Test
    public void testFollowUserIsInvoked() {
        subscriptionController.followUser(followerId, followeeId);
        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserIsInvoked() {
        subscriptionController.unfollowUser(followerId, followeeId);
        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        when(subscriptionService.getFollowers(followeeId, userFilterDto)).thenReturn(expectationUsersDto);

        List<UserDto> actualUsersDto = subscriptionController.getFollowers(followeeId, userFilterDto);

        verify(subscriptionService, times(1)).getFollowers(followeeId, userFilterDto);
        assertEquals(expectationUsersDto, actualUsersDto);
    }

    @Test
    public void testGetFollowersCount() {
        when(subscriptionService.getFollowersCount(followeeId)).thenReturn(expectationCount);

        int actualCount = subscriptionController.getFollowersCount(followeeId);

        verify(subscriptionService, times(1)).getFollowersCount(followeeId);
        assertEquals(expectationCount, actualCount);
    }

    @Test
    public void testGetFollowing() {
        when(subscriptionService.getFollowing(followerId, userFilterDto)).thenReturn(expectationUsersDto);

        List<UserDto> actualUsersDto = subscriptionController.getFollowing(followerId, userFilterDto);

        verify(subscriptionService, times(1)).getFollowing(followerId, userFilterDto);
        assertEquals(expectationUsersDto, actualUsersDto);
    }

    @Test
    public void testGetFollowingCount() {
        when(subscriptionService.getFollowingCount(followerId)).thenReturn(expectationCount);

        int actualCount = subscriptionController.getFollowingCount(followerId);

        verify(subscriptionService, times(1)).getFollowingCount(followerId);
        assertEquals(expectationCount, actualCount);
    }
}