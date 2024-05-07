package school.faang.user_service.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Long user_id;
    private Long event_id;
}
