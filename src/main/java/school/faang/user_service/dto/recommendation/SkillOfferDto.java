package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class SkillOfferDto {
    @NonNull
    private Long id;
    @NonNull
    private Long skillId;
}
