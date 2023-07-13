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
    public void testAssertThrowsDataValidationExceptionForMethodFollowUser() {
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUser() {
        followerId = 1;
        followeeId = 2;

        subscriptionController.followUser(followerId, followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testMessageThrowForMethodFollowUser() {
        try {
            subscriptionController.followUser(followerId, followerId);
        } catch (DataValidationException e) {
            assertEquals("You can't subscribe to yourself", e.getMessage());
        }
        verifyNoInteractions(subscriptionService);
    }
}