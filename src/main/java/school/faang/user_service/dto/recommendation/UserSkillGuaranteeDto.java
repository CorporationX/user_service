package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class UserSkillGuaranteeDto {
    private Long id;
    private Long userId;
    private Long skillId;
    private Long guarantorId;
}
