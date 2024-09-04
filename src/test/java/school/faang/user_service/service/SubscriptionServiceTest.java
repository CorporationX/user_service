package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.FollowerEventDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionServiceValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @Test
    @DisplayName("Test when follower is already subscribed to followee")
    public void testFollowUserFollowerSubscribedFollowee() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User id: " + followerId
                + " already followed to the user id: " + followeeId))
                .when(subscriptionServiceValidator).validateFollowUnfollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(followerId, followeeId);
        });
    }

    @Test
    @DisplayName("Test when follower ID is identical to followee ID")
    public void testFollowUserIdenticalIDs() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User cannot follow to himself"))
                .when(subscriptionServiceValidator).validateFollowUnfollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(followerId, followeeId);
        });
    }

    @Test
    @DisplayName("Test when follower or followee is not found")
    public void testFollowUserFollowerOrFolloweeNotFound() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User " + followeeId + " not found"))
                .when(subscriptionServiceValidator).validateFollowUnfollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(followerId, followeeId);
        });
    }

    @Test
    @DisplayName("Test successful follow operation")
    public void testFollowUserIsFollow() {
        long followerId = 1L;
        long followeeId = 2L;

        doNothing().when(subscriptionServiceValidator).validateFollowUnfollowUser(followerId, followeeId);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);

        verify(followerEventPublisher, Mockito.times(1))
                .publish(Mockito.any(FollowerEventDto.class));
    }

    @Test
    @DisplayName("Test when users are not subscribed to each other")
    public void testUnfollowUserUsersAreNotSubscribedToEachOther() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User id: " + followerId
                + " already followed to the user id: " + followeeId))
                .when(subscriptionServiceValidator).validateFollowUnfollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.unfollowUser(followerId, followeeId);
        });
    }

    @Test
    @DisplayName("Test when follower or followee is not found during unfollow")
    public void testUnfollowUserFollowerOrFolloweeNotFound() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User " + followeeId + " not found"))
                .when(subscriptionServiceValidator).validateFollowUnfollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.unfollowUser(followerId, followeeId);
        });
    }

    @Test
    @DisplayName("Test when follower ID is identical to followee ID during unfollow")
    public void testUnfollowUserIdenticalIDs() {
        long followerId = 1L;
        long followeeId = 2L;

        doThrow(new DataValidationException("User cannot follow to himself"))
                .when(subscriptionServiceValidator).validateFollowUnfollowUser(followerId, followeeId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.unfollowUser(followerId, followeeId);
        });
    }

    @Test
    @DisplayName("Test successful unfollow operation")
    public void testUnfollowUserIsUnfollow() {
        long followerId = 1L;
        long followeeId = 2L;

        doNothing().when(subscriptionServiceValidator).validateFollowUnfollowUser(followerId, followeeId);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Test getting followers when followee not found")
    public void testGetFollowersFolloweeNotFound() {
        long followerId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        doThrow(new DataValidationException("User " + followerId + " not found"))
                .when(subscriptionServiceValidator).validateGetFollowers(followerId, userFilterDto);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowers(followerId, userFilterDto);
        });
    }

    @Test
    @DisplayName("Test getting followers when UserFilterDto is null")
    public void testGetFollowersUserFilterDtoIsNull() {
        long followerId = 1L;

        doThrow(new IllegalArgumentException("UserFilterDto cannot be null"))
                .when(subscriptionServiceValidator).validateGetFollowers(followerId, null);

        assertThrows(IllegalArgumentException.class, () -> {
            subscriptionService.getFollowers(followerId, null);
        });
    }

    @Test
    @DisplayName("Test getting followers returns UserDto list")
    public void testGetFollowersReturnedUsersDto() {
        subscriptionService.getFollowers(1L, new UserFilterDto());

        verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(1L);
    }

    @Test
    @DisplayName("Test getting followers count when user not found")
    public void testGetFollowersCountUserNotFound() {
        long followerId = 1L;

        doThrow(new DataValidationException("User " + followerId + " not found"))
                .when(subscriptionServiceValidator).validateExistsById(followerId);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowersCount(followerId);
        });
    }

    @Test
    @DisplayName("Test getting followers count returns correct count")
    public void testGetFollowersCountReturnedUsersCount() {
        long followerId = 1L;

        subscriptionService.getFollowersCount(followerId);

        verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followerId);
    }

    @Test
    @DisplayName("Test getting following when user not found")
    public void testGetFollowingUserNotFound() {
        long followerId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        doThrow(new DataValidationException("User " + followerId + " not found"))
                .when(subscriptionServiceValidator).validateGetFollowing(followerId, userFilterDto);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowing(followerId, new UserFilterDto());
        });
    }

    @Test
    @DisplayName("Test getting following when UserFilterDto is null")
    public void testGetFollowingUserFilterDtoIsNull() {
        long followerId = 1L;

        doThrow(new IllegalArgumentException("UserFilterDto cannot be null"))
                .when(subscriptionServiceValidator).validateGetFollowing(followerId, null);

        assertThrows(IllegalArgumentException.class, () -> {
            subscriptionService.getFollowing(followerId, null);
        });
    }

    @Test
    @DisplayName("Test getting following returns UserDto list")
    public void testGetFollowingReturnedUsersDto() {
        long followeeId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        subscriptionService.getFollowing(followeeId, userFilterDto);

        verify(subscriptionServiceValidator).validateGetFollowing(followeeId, userFilterDto);
    }

    @Test
    @DisplayName("Test getting following count when user not found")
    public void testGetFollowingCountUserNotFound() {
        long followerId = 1L;

        doThrow(new DataValidationException("User " + followerId + " not found"))
                .when(subscriptionServiceValidator).validateExistsById(followerId);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowingCount(followerId);
        });
    }

    @Test
    @DisplayName("Test getting following count returns correct count")
    public void testGetFollowingCountReturnedUsersCount() {
        long followerId = 1L;

        subscriptionService.getFollowingCount(followerId);

        verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
    }
}
