package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.service.exception.DataValidationException;
import school.faang.user_service.service.service.SubscriptionService;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    public void testFollowUser_ThrowsExceptionOnSelfFollow() {
        long userId = 1;
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(userId, userId));
    }

    @Test
    public void testFollowUser_CallsServiceMethod() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionController.followUser(followerId, followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }
}