package school.faang.user_service.dto.user;

import lombok.*;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
