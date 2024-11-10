package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendationDto(
        Long id,
        @NotNull Long authorId,
        @NotNull Long receiverId,
        @NotBlank @Size(max = 4096) String content,
        List<SkillOfferDto> skillOffers,
        @NotNull @PastOrPresent LocalDateTime createdAt
) {
}
