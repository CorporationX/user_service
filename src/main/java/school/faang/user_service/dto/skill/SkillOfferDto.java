package school.faang.user_service.dto.skill;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillOfferDto {
    private Long id;
    private Long skill;
    private Long recommendation;
}
