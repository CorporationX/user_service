package school.faang.user_service.dto.responses;

import java.util.List;

public record ConstraintErrorResponse(List<Violation> violations) {
}
