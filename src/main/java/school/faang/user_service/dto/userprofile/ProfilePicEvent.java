package school.faang.user_service.dto.userprofile;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProfilePicEvent {

    private long userId;
    private String picLink;
    private LocalDateTime timestamp;
}
