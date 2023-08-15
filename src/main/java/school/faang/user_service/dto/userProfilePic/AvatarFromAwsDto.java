package school.faang.user_service.dto.userProfilePic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvatarFromAwsDto {
    private byte[] file;
    private String contentType;
}
