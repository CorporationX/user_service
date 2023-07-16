package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;
    @Spy
    private UserMapper userMapper;
    @InjectMocks
    private SubscriptionController subscriptionController;

    private final long followerId = 2;
    private long followeeId = 1;

    @Test
    public void shouldAddNewFollowerById() {
        Assertions.assertDoesNotThrow(() -> subscriptionController.followUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenIdEquals() {
        followeeId = 2;

        Assertions.assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(0)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldDeleteFollowerById() {
        Mockito.doNothing().when(subscriptionService).unfollowUser(followerId, followeeId);
        Assertions.assertDoesNotThrow(() -> subscriptionController.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenIdEqualsAreWhenUnsubscribing() {
        followeeId = 2;

        Assertions.assertThrows(DataValidationException.class, () -> subscriptionController.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(0)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldReturnFollowersList() {
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> desiredUsers = List.of(
                new UserDto(),
                new UserDto()
        );

        Mockito.when(subscriptionService.getFollowers(followeeId, filter))
                .thenReturn(desiredUsers);

        List<UserDto> users = subscriptionController.getFollowers(followeeId, filter);

        Assertions.assertEquals(desiredUsers, users);
        Mockito.verify(subscriptionService).getFollowers(followeeId, filter);
    }

    @Test
    public void shouldReturnFolloweesList() {
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> desiredUsers = List.of(
                new UserDto(),
                new UserDto()
        );

        Mockito.when(subscriptionService.getFollowing(followerId, filter))
                .thenReturn(desiredUsers);

        List<UserDto> receivedUsers = subscriptionController.getFollowing(followerId, filter);

        Assertions.assertEquals(desiredUsers, receivedUsers);
        Mockito.verify(subscriptionService).getFollowing(followerId, filter);
    }

    @Test
    public void shouldReturnFollowersCount() {
        int desiredFollowersCount = 3;

        Mockito.when(subscriptionService.getFollowersCount(followeeId))
                .thenReturn(desiredFollowersCount);
        int followersCount = subscriptionController.getFollowersCount(followeeId);

        Assertions.assertEquals(desiredFollowersCount, followersCount);
        Mockito.verify(subscriptionService).getFollowersCount(followeeId);
    }

    @Test
    public void shouldReturnFolloweesCount() {
        int desiredFolloweesCount = 3;

        Mockito.when(subscriptionService.getFollowingCount(followerId))
                .thenReturn(desiredFolloweesCount);
        int followeesCount = subscriptionController.getFollowingCount(followerId);

        Assertions.assertEquals(desiredFolloweesCount, followeesCount);
        Mockito.verify(subscriptionService).getFollowingCount(followerId);
    }
}
