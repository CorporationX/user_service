package school.faang.user_service.dto.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ExceptionDto {
    private final String error;
    private final String message;
    private final String path;
    private final int status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;
}
