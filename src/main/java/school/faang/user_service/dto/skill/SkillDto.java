package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    private String title;
    private List<UserSkillGuaranteeDto> guaranteeDtoList;
}