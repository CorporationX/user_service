package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.UserProfilePic;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
}
