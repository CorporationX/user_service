package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String country;
    private String city;
    private Integer experience;
    private List<SkillDto> skills;
}
