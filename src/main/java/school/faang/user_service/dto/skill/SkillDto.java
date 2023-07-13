package school.faang.user_service.dto.skill;

import lombok.Data;
import school.faang.user_service.dto.UserSkillGuaranteeDto;

import java.util.List;

@Data
public class SkillDto {
    private Long id;
    private String title;
    private List<UserSkillGuaranteeDto> guaranteeDtoList;
}
