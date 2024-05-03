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
    SubscriptionValidator validator;

    @Test
    public void testValidateSubscribeUserToHimself() {
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> validator.validateUserTriedFollowHimself(FOLLOWER_ID, FOLLOWER_ID)
        );
        assertEquals("The user " + FOLLOWER_ID + " tried to follow himself!",
                dataValidationException.getMessage());
    }

    @Test
    public void testIsFollowerUserAndFolloweeUserExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> validator.validateIsExists(FOLLOWER_ID, FOLLOWEE_ID)
        );

        assertEquals("User " + FOLLOWER_ID + " subscription to user " +
                FOLLOWEE_ID + " exist", dataValidationException.getMessage());
    }

    @Test
    public void testValidateUnsubscribeUserToHimself() {
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> validator.validateUserTriedUnfollowHimself(FOLLOWER_ID, FOLLOWER_ID)
        );
        assertEquals("The user " + FOLLOWER_ID + " tried to unfollow himself!",
                dataValidationException.getMessage()
        );
    }
}