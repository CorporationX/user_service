package school.faang.user_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ExceptionResponse {
    private final int code;
    private final String error;
}
