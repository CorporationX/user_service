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
import static school.faang.user_service.util.TestUser.FOLLOWEE_ID;
import static school.faang.user_service.util.TestUser.FOLLOWER_ID;

@ExtendWith(MockitoExtension.class)
class SubscriptionValidatorTest {

    @Mock
    SubscriptionRepository subscriptionRepository;
    @InjectMocks
    SubscriptionValidator subscriptionValidator;

    @Test
    public void testValidateSubscribeUserToHimself() {

        when(subscriptionRepository.existsById(FOLLOWER_ID)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> subscriptionValidator.validateUser(FOLLOWER_ID, FOLLOWER_ID)
        );
        assertEquals("IDs cannot be equal!", dataValidationException.getMessage());
    }

    @Test
    public void testIsFollowerUserAndFolloweeUserExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> subscriptionValidator.validateExistsSubscription(FOLLOWER_ID, FOLLOWEE_ID)
        );

        assertEquals("Subscription already exists!", dataValidationException.getMessage());
    }

    @Test
    public void testValidateUnsubscribeUserToHimself() {
        when(subscriptionRepository.existsById(FOLLOWER_ID)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> subscriptionValidator.validateUser(FOLLOWER_ID, FOLLOWER_ID)
        );
        assertEquals("IDs cannot be equal!", dataValidationException.getMessage());
    }
}