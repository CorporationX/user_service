package school.faang.user_service.validator.event;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {

    @InjectMocks
    private EventValidator eventValidator;

    @Test
    @DisplayName("Ошибка валидации если вместо id передали null")
    void testEventIdIsNullOrElseThrowValidationException() {
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventId(null),
                "Event id can't be null");
    }
}