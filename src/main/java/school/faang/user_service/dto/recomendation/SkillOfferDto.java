package school.faang.user_service.dto.recomendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillOfferDto {
    private long id;
    private long skillId;
    private String title;
}
