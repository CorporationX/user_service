package school.faang.user_service.validator;

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
class SubscriptionServiceValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceValidator subscriptionServiceValidator;

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;

    @Test
    void testValidateFollowIds_Success() {
        assertDoesNotThrow(() -> subscriptionServiceValidator.validateFollowIds(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    void testValidateFollowIds_Fail_WhenSameIds() {
        long sameId = 1L;

        DataValidationException exception = assertThrows(DataValidationException.class, () -> subscriptionServiceValidator.validateFollowIds(sameId, sameId));
        assertEquals("You cannot subscribe/unsubscribe to yourself", exception.getMessage());
    }

    @Test
    void testCheckIfAlreadySubscribed_Success() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(false);

        assertDoesNotThrow(() -> subscriptionServiceValidator.checkIfAlreadySubscribed(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    void testCheckIfAlreadySubscribed_Fail_WhenAlreadySubscribed() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> subscriptionServiceValidator.checkIfAlreadySubscribed(FOLLOWER_ID, FOLLOWEE_ID));
        assertEquals("You are already subscribed/unsubscribed to this user", exception.getMessage());
    }
}