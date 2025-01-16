package school.faang.user_service.dto.recommendation;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    Long id;
    Long authorId;
    Long receiverId;
    List<SkillOfferDto> skillOffers;
    String content;
    LocalDateTime createdAt;
}
