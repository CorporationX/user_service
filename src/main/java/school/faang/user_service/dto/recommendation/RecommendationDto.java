package school.faang.user_service.dto.recommendation;

import school.faang.user_service.dto.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationDto(
        Long id,
        Long authorId,
        Long receiverId,
        String content,
        List<SkillOfferDto> skillOffers,
        LocalDateTime createdAt
) {
}
