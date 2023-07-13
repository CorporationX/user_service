package school.faang.user_service.dto.recommendation;

import lombok.Data;

import java.util.List;

@Data
public class SkillDto {
    private Long id;
    private List<UserSkillGuaranteeDto> guaranteeDtoList;
}
