package school.faang.user_service.dto.user;


import lombok.Builder;
import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private Long countryId;
    private String city;
    private Integer experience;
    private List<SkillDto> skills;
}
