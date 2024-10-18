package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Builder;

@Data
@RequiredArgsConstructor
@Builder
public class UserDto {
    private final Long id;
    private final String username;
    private final String email;
}