package school.faang.user_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.enums.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorCode error;
    private final List<Violation> violations;

    public BusinessException(HttpStatus status, ErrorCode error, List<Violation> violations) {
        super(error.getDescription());
        this.status = status;
        this.error = error;
        this.violations = violations;
    }

    public BusinessException(HttpStatus status, ErrorCode error) {
        this(status, error, new ArrayList<>());
    }
}
