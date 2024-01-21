package school.faang.user_service.dto.user;


import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Data
public class UserFilterDto {

    private String userNamePattern;
    private Long countryId;
    private Integer experienceMax;
    private Integer experienceMin;
    private List<SkillDto> skills;


}
