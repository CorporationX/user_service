package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEducationDto(
        @NotNull
        Integer yearFrom,
        Integer yearTo,
        @NotBlank
        String institution,
        String educationLevel,
        String specialization
) {}