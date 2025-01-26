package school.faang.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserProfilePic {
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;
}