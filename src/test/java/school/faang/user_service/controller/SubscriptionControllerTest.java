package school.faang.user_service.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.exceptions.ExceptionMessage.USER_FOLLOWING_HIMSELF_EXCEPTION;
import static school.faang.user_service.exceptions.ExceptionMessage.USER_UNFOLLOWING_HIMSELF_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

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

    @Test
    void followOtherUserTest() {
        //before
        var followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        var followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);


        //when
        subscriptionController.followUser(followerId, followeeId);

        //then
        verify(subscriptionService, times(1)).followUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @Test
    void followYourselfTest() {
        //before
        followeeId = followerId;

        //when
        var actualException = assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followeeId));

        //then
        assertEquals(USER_FOLLOWING_HIMSELF_EXCEPTION.getMessage(), actualException.getMessage());
        verify(subscriptionService, times(0)).followUser(followerId, followeeId);
    }

    @Test
    void unfollowOtherUserTest() {
        //before
        var followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        var followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);


        //when
        subscriptionController.unfollowUser(followerId, followeeId);

        //then
        verify(subscriptionService, times(1)).unfollowUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @Test
    void unfollowYourselfTest() {
        //before
        followeeId = followerId;


        //when
        var actualException = assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followeeId));

        //then
        assertEquals(USER_UNFOLLOWING_HIMSELF_EXCEPTION.getMessage(), actualException.getMessage());
        verify(subscriptionService, times(0)).followUser(followerId, followeeId);
    }

    @Test
    void getFollowersTest() {
        //before
        var filterArgumentCaptor = ArgumentCaptor.forClass(UserFilterDto.class);
        var filter = new UserFilterDto();

        //when
        subscriptionController.getFollowers(followeeId, filter);


        //then
        verify(subscriptionService, times(1)).getFollowers(followeeArgumentCaptor.capture(), filterArgumentCaptor.capture());
        assertEquals(filter, filterArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }
}