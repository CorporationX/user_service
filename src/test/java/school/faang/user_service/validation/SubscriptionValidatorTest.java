package school.faang.user_service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionValidatorTest {

    private final long followerId = 1L;
    private final long followeeId = 2L;

    @Mock
    SubscriptionRepository subscriptionRepository;
    @InjectMocks
    SubscriptionValidator subscriptionValidator;

    @Test
    public void testValidateSubscribeUserToHimself() {

        when(subscriptionRepository.existsById(followerId)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> subscriptionValidator.validateUser(followerId, followerId)
        );
        assertEquals("IDs cannot be equal!", dataValidationException.getMessage());
    }

    @Test
    public void testIsFollowerUserAndFolloweeUserExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> subscriptionValidator.validateExistsSubscription(followerId, followeeId)
        );

        assertEquals("Subscription already exists!", dataValidationException.getMessage());
    }

    @Test
    public void testValidateUnsubscribeUserToHimself() {
        when(subscriptionRepository.existsById(followerId)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> subscriptionValidator.validateUser(followerId, followerId)
        );
        assertEquals("IDs cannot be equal!", dataValidationException.getMessage());
    }
}