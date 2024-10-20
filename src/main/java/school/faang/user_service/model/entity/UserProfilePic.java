package school.faang.user_service.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserProfilePic {
    private String fileId;
    private String smallFileId;
}