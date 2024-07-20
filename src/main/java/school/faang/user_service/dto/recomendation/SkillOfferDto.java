package school.faang.user_service.dto.recomendation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillOfferDto {
    private long id;
    private long skillId;
}
