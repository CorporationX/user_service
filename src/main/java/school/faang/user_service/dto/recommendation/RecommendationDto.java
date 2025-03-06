package school.faang.user_service.dto.recommendation;

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
