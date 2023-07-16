package school.faang.user_service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.RequestValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class EventParticipationRequestValidatorTest {

    @InjectMocks
    private EventParticipationRequestValidator validator;

    @Test
    void whenValidThenNoExcThrown() {
        assertDoesNotThrow(() -> validator.validate("valid"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void whenNullThenThrownExc(String value) {
        assertThrows(RequestValidationException.class, () -> validator.validate(value));
    }
}