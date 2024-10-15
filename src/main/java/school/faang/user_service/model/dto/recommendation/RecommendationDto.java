package school.faang.user_service.model.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendationDto(
        @NotNull
        @Positive
        Long id,

        @NotNull
        @Positive
        Long authorId,

        @NotNull
        @Positive
        Long receiverId,

        @NotBlank
        String content,

        @NotNull
        List<SkillOfferDto> skillOffers,

        @NotNull
        LocalDateTime createdAt
) {
}
