package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;

public record CreateRecommendationRequestDto(
        @NotBlank(message = "Введите сообщение")
        String message,
        @NotBlank(message = "Введите Id для рекомендации")
        Long receiverId
) {
}
