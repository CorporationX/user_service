package school.faang.user_service.validator.subscription;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionValidatorTest {

    private static final long USER_ID_IS_ONE = 1L;
    private static final long USER_ID_IS_TWO = 2L;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Throws ValidationException when subscription is exists and equals shouldExist")
        void whenSubscriptionIsExistAndEqualsShouldExistThenThrowValidationException() {
            String exceptionMessage = "Exception";
            boolean isExistTrue = Boolean.TRUE;

            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).
                    thenReturn(isExistTrue);

            assertThrows(ValidationException.class,
                    () -> subscriptionValidator.validateSubscriptionExistsAndEqualsShouldExistVariable(
                            USER_ID_IS_ONE,
                            USER_ID_IS_TWO,
                            isExistTrue,
                            exceptionMessage),
                    exceptionMessage);
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Success if subscription is exists and not equals shouldExist")
        void whenSubscriptionIsExistAndNotEqualsShouldExistThenNotThrowValidationException() {
            String exceptionMessage = "Exception";

            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).
                    thenReturn(Boolean.TRUE);

            subscriptionValidator.validateSubscriptionExistsAndEqualsShouldExistVariable(
                    USER_ID_IS_ONE,
                    USER_ID_IS_TWO,
                    Boolean.FALSE,
                    exceptionMessage);
        }
    }
}