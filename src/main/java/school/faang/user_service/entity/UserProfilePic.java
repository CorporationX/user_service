package school.faang.user_service.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Embeddable
public class UserProfilePic {
    private String name;
    private String fileId;
    private String smallFileId;
}