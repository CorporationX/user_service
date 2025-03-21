package school.faang.user_service.dto.recommendation;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SkillOfferDto {
    private Long id;
    private Long skillId;
}
