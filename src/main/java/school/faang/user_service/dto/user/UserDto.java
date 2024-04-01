package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import school.faang.user_service.entity.contact.PreferredContact;
import lombok.*;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
    private PreferredContact preference;
}
