package school.faang.user_service.dto.mentorship;

import lombok.Data;

@Data
public class UserDto {

    private long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private Integer experience;
}
