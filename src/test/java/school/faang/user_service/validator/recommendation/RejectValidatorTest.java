package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.validator.Validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RejectValidatorTest {
    private final Validator<RejectionDto> validator = new RejectValidator();

    @Test
    public void testValidatorWithNullRejection() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(null)));
        assertEquals(RejectValidator.MESSAGE_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testValidatorWithBlankRejection() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto("   ")));
        assertEquals(RejectValidator.MESSAGE_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testValidatorWithEmptyRejection() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto("")));
        assertEquals(RejectValidator.MESSAGE_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testValidatorWithFillingRejection() {
        assertDoesNotThrow(() -> validator.validate(getDto("reason")));
    }

    private RejectionDto getDto(String reason) {
        return RejectionDto.builder()
                .reason(reason)
                .build();
    }
}