package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Data
@NoArgsConstructor
public class UserFilterDto {
    private List<String> country;
    private List<String> cities;
    private List<SkillDto> skills;
}
