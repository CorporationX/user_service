package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @InjectMocks
    SubscriptionController subscriptionController;
    @Mock
    SubscriptionService subscriptionService;

    long followerId;
    long followeeId;

    @BeforeEach
    public void setUp() {
        followerId = 2;
        followeeId = 2;
    }

    @Test
    public void testAssertThrow() {
        // Verify that a DataValidationException is thrown
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUser() {
        followerId = 1;
        followeeId = 2;

        // Test case where followerId is not equal to followeeId
        subscriptionController.followUser(followerId, followeeId);

        // Verify that the followUser method in the subscriptionService is called with the correct arguments
        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testMessageThrow() {
        // Test case where followerId is equal to followeeId
        try {
            subscriptionController.followUser(followerId, followerId);
        } catch (DataValidationException e) {
            // Verify that a DataValidationException is thrown
            assertEquals("You can't subscribe to yourself", e.getMessage());
        }

        // Verify that the followUser method in the subscriptionService is not called
        verifyNoInteractions(subscriptionService);
    }

    @Test
    public void testUnfollowUser() {
        followerId = 1;
        followeeId = 2;

        // Test case where followerId is not equal to followeeId
        subscriptionController.unfollowUser(followerId, followeeId);

        // Verify that the unfollowUser method in the subscriptionService is called with the correct arguments
        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserThrow() {
        // Verify that a DataValidationException is thrown
        assertThrows(DataValidationException.class, () ->
                subscriptionController.unfollowUser(followerId, followeeId));

        // Test case where followerId is equal to followeeId
        try {
            subscriptionController.unfollowUser(followerId, followerId);
        } catch (DataValidationException e) {
            // Verify that a DataValidationException is thrown
            assertEquals("You can't unsubscribe to yourself", e.getMessage());
        }

        // Verify that the unfollowUser method in the subscriptionService is not called
        verifyNoInteractions(subscriptionService);
    }
}