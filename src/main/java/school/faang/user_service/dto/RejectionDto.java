package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectionDto(
    @NotBlank(message = "Причина отказа не может быть пустой")
    String reason
) {}
