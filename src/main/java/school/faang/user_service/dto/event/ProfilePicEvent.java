package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfilePicEvent {
    private long userId;
    private String pictureUrl;
}
