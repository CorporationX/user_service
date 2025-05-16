package school.faang.user_service.validation.data;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.exception.DataValidationException;
import java.time.LocalDateTime;

public class DataRequiredLocalDateTimeValidator implements ConstraintValidator<Required, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            throw new DataValidationException("The date cannot be blank");
        }
        return true;
    }
}
