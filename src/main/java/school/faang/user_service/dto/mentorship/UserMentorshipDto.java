package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserMentorshipDto {
    private Long id;
    private String username;
    private String email;
    private String aboutMe;
    private boolean active;
    private Long countryId;
    private String city;
    private Integer experience;
}
