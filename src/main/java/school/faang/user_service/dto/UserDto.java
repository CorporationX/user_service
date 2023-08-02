package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private Integer experience;
}
