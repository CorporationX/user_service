package school.faang.user_service.validation.data;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.exception.DataValidationException;

public class DataRequiredStringValidator implements ConstraintValidator<Required, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            throw new DataValidationException("The text field cannot be empty");
        }
        return true;
    }
}