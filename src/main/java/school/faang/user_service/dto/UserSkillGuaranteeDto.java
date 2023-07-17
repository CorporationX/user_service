package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSkillGuaranteeDto {

    private Long id;
    private Long userId;
    private Long skillId;
    private Long guarantorId;
}
