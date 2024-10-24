package school.faang.user_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendationDto(
        Long id,
        Long authorId,
        Long receiverId,
        String content,
        List<SkillOfferDto> skillOffers,
        LocalDateTime createdAt
) {
}
