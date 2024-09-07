package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private final long followerId = 1L;

    @Test
    void testFollowUser_checkDataValidationException() {
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followerId));
    }

    @Test
    void testFollowUser_Success() {
        long followeeId = 2L;

        subscriptionController.followUser(followerId, followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }
}