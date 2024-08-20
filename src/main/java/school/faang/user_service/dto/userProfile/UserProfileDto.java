package school.faang.user_service.dto.userProfile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {
    private long id;
    private String username;
}
