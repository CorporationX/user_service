package school.faang.user_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.enums.ErrorCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Violation implements Serializable {
    private final Integer code;
    private final String textCode;
    private final String description;
    private final ErrorField errorField;

    public Violation(ErrorCode errorCode, ErrorField errorField) {
        this(errorCode.getCode(), errorCode.name(), errorCode.getDescription(), errorField);
    }

    public Violation(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.name(), errorCode.getDescription(), null);
    }
}
