package school.faang.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
}
