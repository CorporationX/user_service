package school.faang.user_service.controller;


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
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.exception.ExceptionMessage.USER_FOLLOWING_HIMSELF_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.USER_UNFOLLOWING_HIMSELF_EXCEPTION;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionController subscriptionController;

    ArgumentCaptor<Long> followerArgumentCaptor;
    ArgumentCaptor<Long> followeeArgumentCaptor;
    ArgumentCaptor<UserFilterDto> filterArgumentCaptor;

    Long followerId;
    Long followeeId;

    @BeforeEach
    void init() {
        followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        filterArgumentCaptor = ArgumentCaptor.forClass(UserFilterDto.class);

        followerId = 1L;
        followeeId = 2L;
    }

    @DisplayName("Following other user test")
    @Test
    void followOtherUserTest() {
        var followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        var followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);


        subscriptionController.followUser(followerId, followeeId);


        verify(subscriptionService).followUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @DisplayName("Following yourself test")
    @Test
    void followYourselfTest() {
        followeeId = followerId;

        var actualException = assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followeeId));


        assertEquals(USER_FOLLOWING_HIMSELF_EXCEPTION.getMessage(), actualException.getMessage());
        verify(subscriptionService, times(0)).followUser(followerId, followeeId);
    }

    @DisplayName("Unfollowing other user test")
    @Test
    void unfollowOtherUserTest() {
        var followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        var followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);


        subscriptionController.unfollowUser(followerId, followeeId);


        verify(subscriptionService).unfollowUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @DisplayName("Unfollowing yourself test")
    @Test
    void unfollowYourselfTest() {
        followeeId = followerId;


        var actualException = assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followeeId));


        assertEquals(USER_UNFOLLOWING_HIMSELF_EXCEPTION.getMessage(), actualException.getMessage());
        verify(subscriptionService, times(0)).followUser(followerId, followeeId);
    }

    @DisplayName("Getting followers test")
    @Test
    void getFollowersTest() {
        var filter = new UserFilterDto();

        subscriptionController.getFollowers(followeeId, filter);


        verify(subscriptionService).getFollowers(followeeArgumentCaptor.capture(), filterArgumentCaptor.capture());
        assertEquals(filter, filterArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @DisplayName("Getting followers count test")
    @Test
    void getFollowersCountTest() {
        subscriptionController.getFollowersCount(followeeId);


        verify(subscriptionService).getFollowersCount(followeeArgumentCaptor.capture());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @DisplayName("Getting subscriptions of user test")
    @Test
    void getFollowingTest() {
        var filter = new UserFilterDto();


        subscriptionController.getFollowing(followerId, filter);


        verify(subscriptionService).getFollowing(followerArgumentCaptor.capture(), filterArgumentCaptor.capture());
        assertEquals(filter, filterArgumentCaptor.getValue());
        assertEquals(followerId, followerArgumentCaptor.getValue());
    }

    @DisplayName("Getting subscriptions count test")
    @Test
    void getFollowingCountTest() {
        subscriptionController.getFollowingCount(followerId);


        verify(subscriptionService).getFollowingCount(followerArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
    }
}