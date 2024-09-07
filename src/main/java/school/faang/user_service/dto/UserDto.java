package school.faang.user_service.dto;

import lombok.Data;

@Data
public class UserDto {
    private final long id;
    private final String username;
    private final String email;
}
