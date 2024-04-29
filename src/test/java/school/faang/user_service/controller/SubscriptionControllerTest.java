package school.faang.user_service.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.exceptions.ExceptionMessage.USER_FOLLOWING_HIMSELF_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    void followOtherUserTest() {
        //before
        long followerId = 1L;
        long followeeId = 2L;

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
        long followerId = 1L;
        long followeeId = 1L;


        //when
        var actualException = assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followeeId));

        //then
        assertEquals(USER_FOLLOWING_HIMSELF_EXCEPTION.getMessage(), actualException.getMessage());
        verify(subscriptionService, times(0)).followUser(followerId, followeeId);
    }
}