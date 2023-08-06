package school.faang.user_service.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.RequestValidationException;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ObjectValidator {
    
    private final Validator validator;

    public void validate(Object obj) throws RequestValidationException {
        if (obj == null) {
            throw new RequestValidationException("Object cannot be null");
        }
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        if (!constraintViolations.isEmpty()) {
            String violationsList = StringUtils.join(constraintViolations, ", ");
            throw new RequestValidationException("Validation error: " + violationsList);
        }
    }
}
