package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.userFilter.UserFilter;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private List<UserFilter> userFilters;

    @Spy
    private UserMapper userMapper;

    @Test
    public void testFollowUserFollowerSubscribedFollowee() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(false);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(1L, 2L);
        });
    }

    @Test
    public void testFollowUserFollowerOrFolloweeNotFound() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(false);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(1L, 2L);
        });
    }

    @Test
    public void testFollowUserIsFollow() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.existsById(2L))
                .thenReturn(true);
        subscriptionService.followUser(1L, 2L);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(1L, 2L);
    }

    @Test
    public void testUnfollowUserUsersAreNotSubscribedToEachOther() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(false);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.unfollowUser(1L, 2L);
        });
    }

    @Test
    public void testUnfollowUserFollowerOrFolloweeNotFound() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(false);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(1L, 2L);
        });
    }

    @Test
    public void testUnfollowUserIsUnfollow() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.existsById(2L))
                .thenReturn(true);

        subscriptionService.unfollowUser(1L, 2L);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(1L, 2L);
    }

    @Test
    public void testGetFollowersFolloweeNotFound() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(false);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowers(1L, new UserFilterDto());
        });
    }

    @Test
    public void testGetFollowersUserFilterDtoIsNull() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(true);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowers(1L, null);
        });
    }

    @Test
    public void testGetFollowersReturnedUsersDto() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(true);
        subscriptionService.getFollowers(1L, new UserFilterDto());

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(1L);
    }

    @Test
    public void testGetFollowersCountUserNotFound() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(false);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowersCount(1L);
        });
    }

    @Test
    public void testGetFollowersCountReturnedUsersCount() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(true);

        subscriptionService.getFollowersCount(1L);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(1L);
    }

    @Test
    public void testGetFollowingUserNotFound() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(false);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowing(1L, new UserFilterDto());
        });
    }

    @Test
    public void testGetFollowingUserFilterDtoIsNull() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(true);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowing(1L, null);
        });
    }

    @Test
    public void testGetFollowingReturnedUsersDto() {
        Mockito.when(subscriptionRepository
                .existsById(1L)).thenReturn(true);
        subscriptionService.getFollowing(1L, new UserFilterDto());

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(1L);
    }

    @Test
    public void testGetFollowingCountUserNotFound() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(false);

        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionService.getFollowersCount(1L);
        });
    }

    @Test
    public void testGetFollowingCountReturnedUsersCount() {
        Mockito.when(subscriptionRepository.existsById(1L))
                .thenReturn(true);

        subscriptionService.getFollowingCount(1L);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(1L);
    }
}
