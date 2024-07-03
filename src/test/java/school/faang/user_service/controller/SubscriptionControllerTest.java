package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    void testFollowUser_valid() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionController.followUser(followerId, followeeId);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(followerId, followeeId);
        Mockito.verifyNoMoreInteractions(subscriptionService);
    }

    @Test
    void testFollowUser_sameIds() {
        long followerId = 1;
        long followeeId = 1;

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followeeId)
        );
        assertEquals("Unable to follow yourself", e.getMessage());

        Mockito.verifyNoInteractions(subscriptionService);
    }

    @Test
    void testUnfollowUser_valid() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionController.unfollowUser(followerId, followeeId);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
        Mockito.verifyNoMoreInteractions(subscriptionService);
    }

    @Test
    void testUnfollowUser_sameIds() {
        long followerId = 1;
        long followeeId = 1;

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followeeId)
        );
        assertEquals("Unable to unfollow yourself", e.getMessage());

        Mockito.verifyNoInteractions(subscriptionService);
    }
}