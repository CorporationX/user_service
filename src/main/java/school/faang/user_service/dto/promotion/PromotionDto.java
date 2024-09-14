package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDto {
    private Long id;
    private Long promotedUserId;
    private int priorityLevel;
    private int remainingShows;
    private String promotionTarget;
}
