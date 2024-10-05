package school.faang.user_service.dto.promotion;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
