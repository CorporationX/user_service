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
    SubscriptionValidator validator;

    @Test
    public void testValidateSubscribeUserToHimself() {
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> validator.validateUser(followerId, followerId));
        assertEquals("The user " + followerId + " tried to follow himself!",
                dataValidationException.getMessage());
    }

    @Test
    public void testIsFollowerUserAndFolloweeUserExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> validator.validateExistsSubscription(followerId, followeeId));

        assertEquals("User " + followerId + " subscription to user " +
                followeeId + " exist", dataValidationException.getMessage());
    }
}