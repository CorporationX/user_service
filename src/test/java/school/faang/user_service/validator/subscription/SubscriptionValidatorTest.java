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

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    private final long USER_ID_IS_ONE = 1L;
    private final long USER_ID_IS_TWO = 2L;

    @Nested
    class NegativeTests {

        @Nested
        class CheckIfSubscriptionExistsEqualsShouldExistThenThrowExceptionMethod {
            @Test
            @DisplayName("Если существует подписка и она равна параметру shouldExist то выбрасывается ошибка")
            void WhenSubscriptionIsExistAndEqualsShouldExistThenThrowValidationException() {
                String exceptionMessage = "Exception";
                boolean isExistTrue = Boolean.TRUE;

                when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).
                        thenReturn(isExistTrue);

                assertThrows(ValidationException.class,
                        () -> subscriptionValidator.checkIfSubscriptionExistsAndIfEqualsShouldExistThenThrowException(
                                USER_ID_IS_ONE,
                                USER_ID_IS_TWO,
                                isExistTrue,
                                exceptionMessage),
                        exceptionMessage);
            }
        }
    }

    @Nested
    class PositiveTests {

        @Nested
        class CheckIfSubscriptionExistsEqualsShouldExistThenThrowExceptionMethod {
            @Test
            @DisplayName("Если существует подписка и она не равна параметру shouldExist то метод ничего не возвращает")
            void WhenSubscriptionIsExistAndNotEqualsShouldExistThenNotThrowValidationException() {
                String exceptionMessage = "Exception";

                when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).
                        thenReturn(Boolean.TRUE);

                subscriptionValidator.checkIfSubscriptionExistsAndIfEqualsShouldExistThenThrowException(
                        USER_ID_IS_ONE,
                        USER_ID_IS_TWO,
                        Boolean.FALSE,
                        exceptionMessage);
            }
        }
    }
}