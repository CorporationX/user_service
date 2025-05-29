package school.faang.user_service.validation.data;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

public class DataRequiredListValidator implements ConstraintValidator<Required, List<?>> {

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            throw new DataValidationException("The list cannot be empty");
        }
        return true;
    }
}
