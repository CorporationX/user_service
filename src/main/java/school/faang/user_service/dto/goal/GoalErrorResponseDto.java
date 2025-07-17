package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.exception.BusinessException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class GoalErrorResponseDto implements GoalResponse {

    private int code;
    private String textCode;
    private String description;
    private List<Error> errors = new ArrayList<>();

    public GoalErrorResponseDto(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.textCode = errorCode.name();
        this.description = errorCode.getDescription();
    }

    public GoalErrorResponseDto(BusinessException error) {
        this(error.getError());
        this.errors = error.getViolations().stream()
                .map(v -> new GoalErrorResponseDto.Error(
                        v.getCode(),
                        v.getTextCode(),
                        v.getDescription(),
                        v.getErrorField())
                )
                .toList();
    }

    @Data
    @AllArgsConstructor
    public static class Error implements Serializable {
        private int code;
        private String textCode;
        private String description;
        private ErrorField field;

        public Error(ErrorCode errorCode, ErrorField errorField) {
            this.code = errorCode.getCode();
            this.textCode = errorCode.name();
            this.description = errorCode.getDescription();
            this.field = errorField;
        }
    }
}
