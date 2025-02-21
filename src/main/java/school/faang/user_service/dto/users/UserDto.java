package school.faang.user_service.dto.users;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private boolean active;
    private String aboutMe;
}
