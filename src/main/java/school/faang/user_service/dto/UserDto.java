package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
