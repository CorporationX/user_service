package school.faang.user_service.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {
}
