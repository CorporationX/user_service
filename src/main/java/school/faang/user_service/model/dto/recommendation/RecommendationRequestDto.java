package school.faang.user_service.model.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import school.faang.user_service.model.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendationRequestDto(
        @NotNull
        @Positive
        Long id,

        @NotBlank
        String message,

        @NotNull
        RequestStatus status,

        @NotNull
        @NotEmpty
        List<Long> skillsId,

        @NotNull
        @Positive
        long requesterId,

        @NotNull
        @Positive
        long receiverId,

        @NotNull
        LocalDateTime createdAt,

        LocalDateTime updatedAt) {
}
