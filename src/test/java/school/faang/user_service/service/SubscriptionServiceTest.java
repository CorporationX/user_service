package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.ExceptionMessage.REPEATED_SUBSCRIPTION_EXCEPTION;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    SubscriptionRepository subscriptionRepo;

    @InjectMocks
    SubscriptionService subscriptionService;

    ArgumentCaptor<Long> followerArgumentCaptor;
    ArgumentCaptor<Long> followeeArgumentCaptor;

    Long followerId;
    Long followeeId;

    @BeforeEach
    void init() {
        followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        followerId = 1L;
        followeeId = 2L;
    }

    @DisplayName("Following new user test")
    @Test
    void followNewUserTest() {
        when(subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);


        subscriptionService.followUser(followerId, followeeId);


        verify(subscriptionRepo).existsByFollowerIdAndFolloweeId(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());

        verify(subscriptionRepo).followUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @DisplayName("Following the followed user test")
    @Test
    void followFollowedUserTest() {
        when(subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);


        var actualException = assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followeeId));


        assertEquals(REPEATED_SUBSCRIPTION_EXCEPTION.getMessage(), actualException.getMessage());
        verify(subscriptionRepo).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepo, times(0)).followUser(followerId, followeeId);
    }

    @DisplayName("Unfollowing user test")
    @Test
    void unfollowUserTest() {
        subscriptionService.unfollowUser(followerId, followeeId);


        verify(subscriptionRepo).unfollowUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @DisplayName("Getting followers test")
    @Test
    void getFollowersTest() {
        when(subscriptionRepo.findByFolloweeId(followeeId)).thenReturn(Stream.<User>builder().build());


        var actualFollowers = subscriptionService.getFollowers(followeeId, new UserFilterDto());


        verify(subscriptionRepo).findByFolloweeId(followeeArgumentCaptor.capture());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
        assertEquals(new ArrayList<UserDto>(), actualFollowers);
    }

    @DisplayName("Getting followers count test")
    @Test
    void getFollowersCountTest() {
        var followersCount = subscriptionService.getFollowersCount(followeeId);


        verify(subscriptionRepo).findFollowersAmountByFolloweeId(followeeArgumentCaptor.capture());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @DisplayName("Getting subscriptions test")
    @Test
    void getFollowingTest() {
        Stream<User> allFollowing = new ArrayList<User>().stream();
        when(subscriptionRepo.findByFollowerId(followerId)).thenReturn(allFollowing);


        var actualFollowing = subscriptionService.getFollowing(followerId, new UserFilterDto());


        verify(subscriptionRepo).findByFollowerId(followerArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(new ArrayList<UserDto>(), actualFollowing);
    }

    @DisplayName("Getting subscriptions count test")
    @Test
    void getFollowingCountTest() {
        subscriptionService.getFollowingCount(followerId);


        verify(subscriptionRepo).findFolloweesAmountByFollowerId(followerArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
    }
}