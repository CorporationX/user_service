package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProfilePicEvent {
    private Long userId;
    private String profilePicUrl;
}