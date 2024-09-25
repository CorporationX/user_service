package school.faang.user_service.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Embeddable
@NoArgsConstructor
public class UserProfilePic {
    private String fileId;
    private String smallFileId;
}