package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record RejectionDto (@NotBlank(message = "There must be a reason for rejection.") String reason) {}
