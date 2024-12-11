package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithoutFollowersDto {
    private Long userId;
    private String username;
    private String fileId;
    private String smallFileId;
}
