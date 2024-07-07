package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class SkillOfferDto {
    private long id;
    private long skillId;
}
