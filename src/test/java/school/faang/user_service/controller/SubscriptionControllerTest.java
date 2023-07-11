package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    public void shouldAddNewFollowerById() {
        long followerId = 2;
        long followeeId = 1;

        Mockito.doNothing().when(subscriptionService).followUser(followerId, followeeId);
        Assertions.assertDoesNotThrow(() -> subscriptionController.followUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(1)).followUser(followerId, followeeId);
    }
}
