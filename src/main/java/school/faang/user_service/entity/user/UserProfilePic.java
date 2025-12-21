package school.faang.user_service.entity.user;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePic {
    private String fileId;
    private String smallFileId;
    private String generatedFileUrl;
    private String smallGeneratedFileUrl;
}