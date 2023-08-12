package school.faang.user_service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidCurrencyValidatorTest {

    @InjectMocks
    private ValidCurrencyValidator validator;


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"usd", "USD","EuR"})
    void whenValidValueThenTrue(String value) {
        boolean isValid = validator.isValid(value, null);

        assertTrue(isValid);
    }

    @Test
    void whenInvalidValueThenFalse() {
        boolean isValid = validator.isValid("smth", null);

        assertFalse(isValid);
    }
}