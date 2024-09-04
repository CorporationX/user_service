package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SubscriptionValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

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
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        Exception exception = assertThrows(DataValidationException.class,
                () -> subscriptionValidator.validateFollowSubscription(followerId, followeeId));

        assertEquals("User 1 is already subscribed to user 2.", exception.getMessage());
    }

    @Test
    void validateFollowSubscription_SubscriptionDoesNotExists() {
        long followerId = 1L, followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertDoesNotThrow(() -> subscriptionValidator.validateFollowSubscription(followerId, followeeId));
    }

    @Test
    void validateUnfollowSubscription_SubscriptionDoesNotExists() {
        long followerId = 1L, followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        Exception exception = assertThrows(DataValidationException.class,
                () -> subscriptionValidator.validateUnfollowSubscription(followerId, followeeId));

        assertEquals("User 1 is already unsubscribe to user 2.", exception.getMessage());
    }

    @Test
    void validateUnfollowSubscription_SubscriptionExists() {
        long followerId = 1L, followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertDoesNotThrow(() -> subscriptionValidator.validateUnfollowSubscription(followerId, followeeId));
    }
}