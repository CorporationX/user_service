package school.faang.user_service.service.subscription;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SubscriptionRequirementsException;
import school.faang.user_service.publis.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.service.filters.UserAboutFilter;
import school.faang.user_service.service.filters.UserExperienceMinFilter;
import school.faang.user_service.service.filters.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


public class SubscriptionServiceTest {
    @Mock
    private final List<UserFilter> userFilters = new ArrayList<>();
    @Mock
    SubscriptionRepository subscriptionRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    FollowerEventPublisher followerEventPublisher;

    @InjectMocks
    SubscriptionService subscriptionService;


    long firstUserId = 1L;
    long secondUserId = 2L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userFilters.add(mock(UserExperienceMinFilter.class));
        userFilters.add(mock(UserAboutFilter.class));
        reset(userRepository);
    }

    @Test
    public void testFollowUserSuccessful() {
        User followerUser = User.builder()
                .id(firstUserId)
                .username("follower")
                .build();

        when(userRepository.existsById(firstUserId)).thenReturn(true);
        when(userRepository.existsById(secondUserId)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId)).thenReturn(false);
        when(userRepository.findById(firstUserId)).thenReturn(Optional.of(followerUser));

        subscriptionService.followUser(firstUserId, secondUserId);

        verify(subscriptionRepository, times(1)).followUser(firstUserId, secondUserId);
    }

    @Test
    public void testFollowUserDoesntExist() {
        when(userRepository.existsById(firstUserId)).thenReturn(false);
        Assert.assertThrows(SubscriptionRequirementsException.class, () -> subscriptionService.followUser(firstUserId, secondUserId));
    }


    @Test
    public void testFollowUserThrowsExceptionWhenIdIsSame() {
        when(userRepository.existsById(firstUserId)).thenReturn(true);

        DataValidationException thrown = Assert.assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(firstUserId, firstUserId)
        );

        Assert.assertEquals("You cannot subscribe to yourself", thrown.getMessage());
    }


    @Test
    public void testFollowUserThrowsExceptionWhenSubExist() {
        when(userRepository.existsById(firstUserId)).thenReturn(true);
        when(userRepository.existsById(secondUserId)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId)).thenReturn(true);

        DataValidationException thrown = Assert.assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(firstUserId, secondUserId)
        );

        assertEquals("Already followed", thrown.getMessage());
    }


    @Test
    public void testUnfollowUserSuccessful() {
        when(userRepository.existsById(firstUserId)).thenReturn(true);
        when(userRepository.existsById(secondUserId)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId)).thenReturn(true);
        subscriptionService.unfollowUser(firstUserId, secondUserId);
        verify(subscriptionRepository, times(1)).unfollowUser(firstUserId, secondUserId);
    }

    @Test
    public void testUnfollowUserThrowsException() {
        when(userRepository.existsById(firstUserId)).thenReturn(true);
        when(userRepository.existsById(secondUserId)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId)).thenReturn(false);

        DataValidationException thrown = Assert.assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(firstUserId, secondUserId)
        );

        assertEquals("You are not following this user", thrown.getMessage());
    }

    @Test
    public void testGetFollowersCountSuccessful() {
        when(userRepository.existsById(firstUserId)).thenReturn(true);
        when(subscriptionRepository.findFollowersAmountByFolloweeId(firstUserId)).thenReturn(10);
        subscriptionService.getFollowersCount(firstUserId);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(firstUserId);
        Assert.assertEquals(10, subscriptionService.getFollowersCount(firstUserId));
    }

    @Test
    public void testGetFolloweeCountSuccessful() {
        when(userRepository.existsById(firstUserId)).thenReturn(true);
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

        when(userRepository.existsById(firstUserId)).thenReturn(true);
        when(subscriptionRepository.findByFolloweeId(user1.getId())).thenReturn(followers.stream());
        List<User> result = subscriptionService.getFollowers(user1.getId(), filter);
        Assert.assertEquals(followers, result);
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

        when(userRepository.existsById(firstUserId)).thenReturn(true);
        when(subscriptionRepository.findByFollowerId(user1.getId())).thenReturn(following.stream());
        Assert.assertEquals(following, subscriptionService.getFollowing(user1.getId(), filter));
    }
}
