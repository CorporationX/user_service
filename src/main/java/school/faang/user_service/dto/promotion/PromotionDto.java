package school.faang.user_service.dto.promotion;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDto {

    @NonNull
    private Long userId;

    @NonNull
    private Integer priority;

    @NonNull
    private Integer showCount;

    @NonNull
    private PromotionTarget target;
}
