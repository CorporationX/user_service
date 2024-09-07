package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filters.UserFilter;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class SubscriptionServiceTest {

    @Mock
    private final List<UserFilter> userFilters = new ArrayList<>();
    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    SubscriptionService subscriptionService;


    long firstUserId = 1L;
    long secondUserId = 2L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFollowUserSuccessful() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId)).thenReturn(false);
        subscriptionService.followUser(firstUserId, secondUserId);
        verify(subscriptionRepository, times(1)).followUser(firstUserId, secondUserId);
    }

    @Test
    public void testFollowUserThrowsException() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, firstUserId)).thenReturn(true);
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(firstUserId, firstUserId));
    }

    @Test
    public void testFollowUserThrowsExceptionWhenSubExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, firstUserId)).thenReturn(true);
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(firstUserId, firstUserId));
    }

    @Test
    public void testUnfollowUserSuccessful() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId)).thenReturn(true);
        subscriptionService.unfollowUser(firstUserId, secondUserId);
        verify(subscriptionRepository, times(1)).unfollowUser(firstUserId, secondUserId);
    }

    @Test
    public void testUnfollowUserThrowsException() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, firstUserId)).thenReturn(false);
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(firstUserId, firstUserId));
    }

    @Test
    public void testGetFollowersCountSuccessful() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(firstUserId)).thenReturn(10);
        subscriptionService.getFollowersCount(firstUserId);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(firstUserId);
        Assert.assertEquals(10, subscriptionService.getFollowersCount(firstUserId));
    }

    @Test
    public void testGetFolloweeCountSuccessful() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(firstUserId)).thenReturn(10);
        subscriptionService.getFollowingCount(firstUserId);
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(firstUserId);
        Assert.assertEquals(10, subscriptionService.getFollowingCount(firstUserId));
    }

    @Test
    public void testGetFollowersList() {
        User user1 = new User();
        User user2 = new User();

        user1.setId(firstUserId);
        user2.setId(secondUserId);

        List<User> followers = new ArrayList<>();
        followers.add(user2);
        UserFilterDto filter = new UserFilterDto();

        when(subscriptionRepository.findByFolloweeId(user1.getId())).thenReturn(followers.stream());
        Assert.assertEquals(followers, subscriptionService.getFollowers(user1.getId(), filter));
    }

    @Test
    public void testGetFollowingsList() {
        User user1 = new User();
        User user2 = new User();

        user1.setId(firstUserId);
        user2.setId(secondUserId);

        List<User> following = new ArrayList<>();
        following.add(user2);
        UserFilterDto filter = new UserFilterDto();

        when(subscriptionRepository.findByFollowerId(user1.getId())).thenReturn(following.stream());
        Assert.assertEquals(following, subscriptionService.getFollowing(user1.getId(), filter));
    }
}
