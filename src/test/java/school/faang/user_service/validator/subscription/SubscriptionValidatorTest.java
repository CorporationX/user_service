package school.faang.user_service.validator.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.exceptions.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class SubscriptionValidatorTest {

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Test
    void validateUserIds_DifferentIds() {
        long followerId = 1L, followeeId = 2L;

        assertDoesNotThrow(() -> subscriptionValidator.validateUserIds(followerId, followeeId));
    }

    @Test
    void validateUserIds_IdenticalIds() {
        long followerId = 1L, followeeId = 1L;
        String correctMessage = "User 1 is trying to unsubscribe to himself";

        Exception exception = assertThrows(DataValidationException.class,
                () -> subscriptionValidator.validateUserIds(followerId, followeeId));

        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void validateFollowSubscription_SubscriptionExists() {
        long followerId = 1L, followeeId = 2L;
        boolean exists = true;

        Exception exception = assertThrows(DataValidationException.class,
                () -> subscriptionValidator.validateFollowSubscription(exists, followerId, followeeId));

        assertEquals("User 1 is already subscribed to user 2.", exception.getMessage());
    }

    @Test
    void validateFollowSubscription_SubscriptionDoesNotExists() {
        long followerId = 1L, followeeId = 2L;
        boolean exists = false;

        assertDoesNotThrow(() -> subscriptionValidator.validateFollowSubscription(exists, followerId, followeeId));
    }

    @Test
    void validateUnfollowSubscription_SubscriptionDoesNotExists() {
        long followerId = 1L, followeeId = 2L;
        boolean exists = false;

        Exception exception = assertThrows(DataValidationException.class,
                () -> subscriptionValidator.validateUnfollowSubscription(exists, followerId, followeeId));

        assertEquals("User 1 is already unsubscribe to user 2.", exception.getMessage());
    }

    @Test
    void validateUnfollowSubscription_SubscriptionExists() {
        long followerId = 1L, followeeId = 2L;
        boolean exists = true;

        assertDoesNotThrow(() -> subscriptionValidator.validateUnfollowSubscription(exists, followerId, followeeId));
    }
}