package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.validator.SubscriptionServiceValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SubscriptionServiceValidator subscriptionServiceValidator;

    @Test
    public void testFollowUserFollowerSubscribedFollowee() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User id: " + followerId
                + " already followed to the user id: " + followeeId))
                .when(subscriptionServiceValidator).validFollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(followerId, followeeId);
        });
    }

    @Test
    public void testFollowUserFollowerOrFolloweeNotFound() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User " + followeeId + " not found"))
                .when(subscriptionServiceValidator).validFollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(followerId, followeeId);
        });
    }

    @Test
    public void testFollowUserIsFollow() {
        long followerId = 1L;
        long followeeId = 2L;

        doNothing().when(subscriptionServiceValidator).validFollowUser(followerId, followeeId);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserUsersAreNotSubscribedToEachOther() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User id: " + followerId
                + " already followed to the user id: " + followeeId))
                .when(subscriptionServiceValidator).validUnfollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.unfollowUser(followerId, followeeId);
        });
    }

    @Test
    public void testUnfollowUserFollowerOrFolloweeNotFound() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User " + followeeId + " not found"))
                .when(subscriptionServiceValidator).validUnfollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.unfollowUser(followerId, followeeId);
        });
    }

    @Test
    public void testUnfollowUserIsUnfollow() {
        long followerId = 1L;
        long followeeId = 2L;

        doNothing().when(subscriptionServiceValidator).validUnfollowUser(followerId, followeeId);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowersFolloweeNotFound() {
        long followerId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        doThrow(new DataValidationException("User " + followerId + " not found"))
                .when(subscriptionServiceValidator).validGetFollowers(followerId, userFilterDto);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowers(followerId, userFilterDto);
        });
    }

    @Test
    public void testGetFollowersUserFilterDtoIsNull() {
        long followerId = 1L;

        doThrow(new IllegalArgumentException("UserFilterDto cannot be null"))
                .when(subscriptionServiceValidator).validGetFollowers(followerId, null);

        assertThrows(IllegalArgumentException.class, () -> {
            subscriptionService.getFollowers(followerId, null);
        });
    }

    @Test
    public void testGetFollowersReturnedUsersDto() {
        subscriptionService.getFollowers(1L, new UserFilterDto());

        verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(1L);
    }

    @Test
    public void testGetFollowersCountUserNotFound() {
        long followerId = 1L;

        doThrow(new DataValidationException("User " + followerId + " not found"))
                .when(subscriptionServiceValidator).validGetFollowersCount(followerId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowersCount(followerId);
        });
    }

    @Test
    public void testGetFollowersCountReturnedUsersCount() {
        long followerId = 1L;

        subscriptionService.getFollowersCount(followerId);

        verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followerId);
    }

    @Test
    public void testGetFollowingUserNotFound() {
        long followerId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        doThrow(new DataValidationException("User " + followerId + " not found"))
                .when(subscriptionServiceValidator).validGetFollowing(followerId, userFilterDto);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowing(followerId, new UserFilterDto());
        });
    }

    @Test
    public void testGetFollowingUserFilterDtoIsNull() {
        long followerId = 1L;

        doThrow(new IllegalArgumentException("UserFilterDto cannot be null"))
                .when(subscriptionServiceValidator).validGetFollowing(followerId, null);

        assertThrows(IllegalArgumentException.class, () -> {
            subscriptionService.getFollowing(followerId, null);
        });
    }

    @Test
    public void testGetFollowingReturnedUsersDto() {
        long followeeId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        subscriptionService.getFollowing(followeeId, userFilterDto);

        verify(subscriptionServiceValidator).validGetFollowing(followeeId, userFilterDto);
    }

    @Test
    public void testGetFollowingCountUserNotFound() {
        long followerId = 1L;

        doThrow(new DataValidationException("User " + followerId + " not found"))
                .when(subscriptionServiceValidator).validGetFollowingCount(followerId);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowingCount(followerId);
        });
    }

    @Test
    public void testGetFollowingCountReturnedUsersCount() {
        long followerId = 1L;

        subscriptionService.getFollowingCount(followerId);

        verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
    }
}
