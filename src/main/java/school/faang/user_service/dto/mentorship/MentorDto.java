package school.faang.user_service.dto.mentorship;

import java.util.List;
import lombok.Data;

@Data
public class MentorDto {
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private Integer experience;
    private List<MenteeDto> mentees;
}
