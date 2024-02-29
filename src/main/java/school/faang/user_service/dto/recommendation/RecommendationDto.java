package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDto {
    private Long Id;
    private Long authorId;
    private Long receiverId;
    private String content;
    private LocalDateTime createdAt;
    private List<SkillOfferDto> skillOffers;
}
