package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.RequestStatus;

public record RequestFilterDto(@NotNull(message = "Status cannot be empty") RequestStatus status, String messagePattern) {}
