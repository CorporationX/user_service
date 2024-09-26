package school.faang.user_service.exceptionDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExceptionDto {
    private String error;
    private String message;
    private String path;
    private int code;
}
