package school.faang.user_service.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.skill.SubscriptionValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {

    private SubscriptionValidator validator = new SubscriptionValidator();

    @Mock
    private SubscriptionRepository repository;

    Long firstUserId;
    Long secondUserId;

    @BeforeEach
    void setUp() {
        firstUserId = 1L;
        secondUserId = 2L;
    }

    @Test
    public void givenNotValidIdWhenValidateId() {
        firstUserId = -1L;
        secondUserId = -1L;

        assertThrows(DataValidationException.class, () ->
                validator.validateId(firstUserId, secondUserId));
    }

    @Test
    public void givenOneNotValidIdWhenValidateId() {
        firstUserId = -1L;

        assertThrows(DataValidationException.class, () ->
                validator.validateId(firstUserId));
    }

    @Test
    public void testSubscribeAndUnsubscribeToYourself() {
        Long secondUserId = 1L;

        assertThrows(DataValidationException.class, () ->
                validator.checkingSubscription(firstUserId, secondUserId));
    }

    @Test
    public void testIsSubscriberShouldThrowException() {
        Mockito.when(repository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId))
                .thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                validator.isSubscriber(firstUserId, secondUserId, repository));
        assertEquals("You are already subscribed/unsubscribed to this user",
                exception.getMessage());
    }

    @Test
    public void testIsSubscriberShouldNotThrowException() {
        Mockito.when(repository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId))
                .thenReturn(false);

        assertDoesNotThrow(() -> validator.isSubscriber(
                firstUserId,
                secondUserId,
                repository));
    }

    @Test
    public void testIsNotSubscriberShouldThrowException() {
        Mockito.when(repository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId))
                .thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                validator.isNotSubscriber(firstUserId, secondUserId, repository));
        assertEquals("You are already subscribed/unsubscribed to this user",
                exception.getMessage());
    }

    @Test
    public void testIsNotSubscriberShouldNotThrowException() {
        Mockito.when(repository.existsByFollowerIdAndFolloweeId(firstUserId, secondUserId))
                .thenReturn(true);

        assertDoesNotThrow(() -> validator.isNotSubscriber(
                firstUserId,
                secondUserId,
                repository));
    }
}
