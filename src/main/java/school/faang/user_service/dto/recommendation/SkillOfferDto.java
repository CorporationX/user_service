package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.NonNull;

@Data
public class SkillOfferDto {
    @NonNull
    private Long id;
    @NonNull
    private Long skillId;
}
