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
class SubscriptionValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    //Valid subscription
    @Test
    void validateSubscription_WhenFollowerIdEqualsFolloweeId_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 1;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateSubscriptionId(followerId, followeeId);
        });

        assertEquals("Нельзя подписаться на самого себя", dataValidationException.getMessage());
    }

    @Test
    void validateSubscription_WhenSubscriptionExists_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateSubscriptionExits(followerId, followeeId);
        });

        assertEquals("Такая подписка уже существует", dataValidationException.getMessage());
    }

    @Test
    void validateSubscription_WhenValid_ShouldNotThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        when(subscriptionRepository.existsById(followerId)).thenReturn(true);
        when(subscriptionRepository.existsById(followeeId)).thenReturn(true);


        assertDoesNotThrow(() -> {
            subscriptionValidator.validateSubscriptionExits(followerId, followeeId);
        });
    }

    @Test
    void validateSubscription_WhenFollowerDoesNotExist_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        when(subscriptionRepository.existsById(followerId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateSubscriptionExits(followerId, followeeId);
        });

        assertEquals("Пользователь не существует", exception.getMessage());
    }

    @Test
    void validateSubscription_WhenFolloweeDoesNotExist_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        when(subscriptionRepository.existsById(followerId)).thenReturn(true);
        when(subscriptionRepository.existsById(followeeId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateSubscriptionExits(followerId, followeeId);
        });

        assertEquals("Нельзя подписаться на несуществующего пользователя", exception.getMessage());
    }

    //Valid unsubscription
    @Test
    void validateUnsubscription_WhenFollowerIdEqualsFolloweeId_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 1;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateUnsubscriptionId(followerId, followeeId);
        });

        assertEquals("Нельзя отписаться от самого себя", dataValidationException.getMessage());
    }

    @Test
    void validateUnsubscription_WhenSubscriptionDoesNotExists_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateUnsubscriptionExits(followerId, followeeId);
        });

        assertEquals("Такoй подписки не существует", dataValidationException.getMessage());
    }

    @Test
    void validateUnsubscription_WhenValid_ShouldNotThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        when(subscriptionRepository.existsById(followerId)).thenReturn(true);
        when(subscriptionRepository.existsById(followeeId)).thenReturn(true);

        assertDoesNotThrow(() -> {
            subscriptionValidator.validateUnsubscriptionExits(followerId, followeeId);
        });
    }

    @Test
    void validateUnsubscription_WhenFollowerDoesNotExist_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        when(subscriptionRepository.existsById(followerId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateUnsubscriptionExits(followerId, followeeId);
        });

        assertEquals("Пользователь не существует", exception.getMessage());
    }

    @Test
    void validateUnsubscription_WhenFolloweeDoesNotExist_ShouldThrowException() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        when(subscriptionRepository.existsById(followerId)).thenReturn(true);
        when(subscriptionRepository.existsById(followeeId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateUnsubscriptionExits(followerId, followeeId);
        });

        assertEquals("Нельзя отписаться от несуществующего пользователя", exception.getMessage());
    }
}
