package school.faang.user_service.Utils;

import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.Optional;

public class ValidatorUtil {

    private ValidatorUtil() {

    }

    public static <T> T validateOptional(Optional<T> optional, String exceptionMsg) {
        return optional.orElseThrow(() -> new EntityNotFoundException(exceptionMsg));
    }

    public static <T> void validateNull(T val) {
        if (val == null) {
            throw new DataValidationException("Value can't be null");
        }
    }
}
