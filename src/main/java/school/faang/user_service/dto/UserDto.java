package school.faang.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDto {
    private final Long id;
    private final String username;
    private final String email;
}
