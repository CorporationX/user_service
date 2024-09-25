package school.faang.user_service.dto.promotion;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionDto {
    private Long id;
    private Long promotedUserId;
    private int priorityLevel;
    private int remainingShows;
    private String promotionTarget;
}
