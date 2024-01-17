package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Test
    public void validateUserIds_whenIdEquals_ThrowsException() {
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> subscriptionValidator.validateUserIds(1L, 1L));

        assertEquals("FollowerId и followeeId не могут совпадать", dataValidationException.getMessage());
    }

    @Test
    public void validateUserIds_whenIdsDifferent_DoesNotThrowsException() {
        assertDoesNotThrow(() -> subscriptionValidator.validateUserIds(1L, 2L));
    }

    @Test
    public void validateSubscription_whenUserDoesNotExist_ThrowsException() {
        doThrow(new DataValidationException("Нет пользователя с таким айди"))
                .when(subscriptionService).validateExitsUsers(1, 2);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> subscriptionValidator.validateUserIds(1L, 2L));

        assertEquals("Нет пользователя с таким айди", dataValidationException.getMessage());
    }

    @Test
    public void validateSubscription_whenSubscriptionExists_ReturnsTrue() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

        assertTrue(subscriptionValidator.validateSubscription(1L, 2L));
    }

    @Test
    public void validateSubscription_whenSubscriptionDoesNotExists_ReturnsFalse() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);

        assertFalse(subscriptionValidator.validateSubscription(1L, 2L));
    }
}
