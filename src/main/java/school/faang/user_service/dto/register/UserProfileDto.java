package school.faang.user_service.dto.register;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
}
