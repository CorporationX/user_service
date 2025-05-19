package school.faang.user_service.validation.data;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.exception.DataValidationException;

public class DataRequiredLongValidator implements ConstraintValidator<Required, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null || value <= 0) {
            throw new DataValidationException("The numeric field must be greater than zero");
        }
        return true;
    }
}
