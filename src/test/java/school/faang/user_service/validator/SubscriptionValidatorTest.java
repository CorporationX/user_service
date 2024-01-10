package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Test
    void validateSubscription_WhenFollowerIdEqualsFolloweeId_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 1;

        assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateSubscription(followerId, followeeId);
        });
    }

    @Test
    void validateSubscription_WhenSubscriptionExists_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateSubscription(followerId, followeeId);
        });
    }

    @Test
    void validateSubscription_WhenValid_ShouldNotThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertDoesNotThrow(() -> {
            subscriptionValidator.validateSubscription(followerId, followeeId);
        });
    }
}
