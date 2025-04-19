package school.faang.user_service.dto;

import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String userName;
    private String email;
    private String phone;
    private boolean active;
    private String aboutMe;
    private boolean banned;
}
