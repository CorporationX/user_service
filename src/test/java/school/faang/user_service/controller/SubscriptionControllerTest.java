package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionController subscriptionController;

    @Test
    void testFollowUser(){
        subscriptionController.followUser(22, 23);
        Mockito.verify(subscriptionService, Mockito.times(1)).followUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testUnfollowUser(){
        subscriptionController.unfollowUser(22, 23);
        Mockito.verify(subscriptionService, Mockito.times(1)).unfollowUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testControllerUnFollowUserByNull(){
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(-33, 1));
    }

    @Test
    void testControllerFollowUserByNull(){
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(-33, 1));
    }
}
