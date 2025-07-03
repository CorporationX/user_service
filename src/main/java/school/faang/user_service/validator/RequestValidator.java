package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class RequestValidator {
    public static void validateStringNotEmpty(String value, String paramName) {
        if (StringUtils.isBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    public static void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}
