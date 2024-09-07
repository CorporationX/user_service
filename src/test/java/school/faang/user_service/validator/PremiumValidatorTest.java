package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.PremiumAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PremiumValidatorTest {

    private final PremiumValidator premiumValidator = new PremiumValidator();
    private final Long id = 1L;

    @Test
    void validate_ExistsPremium() {
        String message = "Premium already exists with id 1";
        boolean existPremium = true;

        var exception = assertThrows(PremiumAlreadyExistsException.class,
                () -> premiumValidator.validate(id, existPremium));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void validate_NotExistsPremium() {
        boolean existPremium = false;
        assertDoesNotThrow(() -> premiumValidator.validate(id, existPremium));
    }
}