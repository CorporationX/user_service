package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFilterDto {
    private Long id;
    private String userNamePattern;
    private Long countryId;
    private Integer experienceMax;
    private Integer experienceMin;
    private List<SkillDto> skills;
    private String username;
    private String email;
    private String phone;
    private String password;
    private Boolean active;
    private String aboutMe;
    private String city;
}
