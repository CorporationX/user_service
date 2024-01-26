package school.faang.user_service.util;

import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

public class Utils {
    public static String getErrMsgBindingRes(BindingResult bindingResult) {
        return bindingResult
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
    }
}
