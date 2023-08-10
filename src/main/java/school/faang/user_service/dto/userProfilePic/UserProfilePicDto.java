package school.faang.user_service.dto.userProfilePic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfilePicDto {
    private Long userId;
    private String fileId;
    private String smallFileId;
}
