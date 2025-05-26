package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationResponseDto(
        Long id,

        @NotBlank(message = "Message cannot be blank")
        String message,

        String status,

        @NotEmpty(message = "Skills cannot be empty")
        List<String> skills,

        @NotNull(message = "Requester ID cannot be null")
        Long requesterId,

        @NotNull(message = "Receiver ID cannot be null")
        Long receiverId,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
