package school.faang.user_service.validator;

import school.faang.user_service.entity.Violation;

import java.util.List;

public record ValidationResult(boolean isValid, List<Violation> violations) {
}
