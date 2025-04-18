package school.faang.user_service.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UserFilterDto(
        @Size(max = 255, message = "Name pattern should not exceed 255 characters")
        String namePattern,
        @Size(max = 20, message = "Phone pattern should not exceed 20 characters")
        String phonePattern,
        @PositiveOrZero(message = "Minimum experience cannot be less than 0")
        int experienceMin,
        int experienceMax) {
}