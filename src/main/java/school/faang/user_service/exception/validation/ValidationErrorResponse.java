package school.faang.user_service.exception.validation;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {

}
