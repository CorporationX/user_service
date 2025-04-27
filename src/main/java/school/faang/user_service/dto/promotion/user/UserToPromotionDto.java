package school.faang.user_service.dto.promotion.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserToPromotionDto {
    private Long id;
    private String username;
    private String aboutMe;
    private SkillDto skillDto;
}
