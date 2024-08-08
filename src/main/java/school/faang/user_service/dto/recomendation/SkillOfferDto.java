package school.faang.user_service.dto.recomendation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Information about skill offer")
public class SkillOfferDto {
    @Schema(description = "skill offer identifier")
    private long id;
    @Schema(description = "skill identifier")
    private long skillId;
}
