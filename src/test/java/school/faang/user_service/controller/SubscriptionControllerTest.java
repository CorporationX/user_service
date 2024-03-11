package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;
    @InjectMocks
    private SubscriptionController subscriptionController;

    private final long followerId = 2;
    private long followeeId = 1;

    @Test
    public void shouldAddNewFollowerById() {
        Assertions.assertDoesNotThrow(() -> subscriptionController.followUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenIdEqualsAreWhenSubscribing() {
        followeeId = 2;

        Assertions.assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(0)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldDeleteFollowerById() {
        Mockito.doNothing().when(subscriptionService).unfollowUser(followerId, followeeId);
        Assertions.assertDoesNotThrow(() -> subscriptionController.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenIdEqualsAreWhenUnsubscribing() {
        followeeId = 2;

        Assertions.assertThrows(DataValidationException.class, () -> subscriptionController.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionService, Mockito.times(0)).unfollowUser(followerId, followeeId);
    }
}
