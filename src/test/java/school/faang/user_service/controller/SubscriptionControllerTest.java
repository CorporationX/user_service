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
        subscriptionController.followUser(22L, 23L);
        Mockito.verify(subscriptionService, Mockito.times(1)).followUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testControllerFollowUserByNull(){
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(-33L, 1L));
    }
}
