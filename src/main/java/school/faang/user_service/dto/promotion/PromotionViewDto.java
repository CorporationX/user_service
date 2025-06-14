package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.entity.promotion.enums.ViewWidth;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PromotionViewDto {
    private Long id;
    private Plan promotionType;
    private Integer numPromotedViews;
    private ViewWidth viewWidth;
}
