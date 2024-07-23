package school.faang.user_service.dto.userdto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String username;
    private String city;
    private String email;
}
