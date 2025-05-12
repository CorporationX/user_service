package school.faang.user_service.validation.data;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.exception.DataValidationException;

public class DataRequiredLongValidator implements ConstraintValidator<Required, Long> {
    private static final long MIN_VALUE = 1;
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null || value < MIN_VALUE) {
            throw new DataValidationException("The numeric field must be greater than zero");
        }
        return true;
    }
}
