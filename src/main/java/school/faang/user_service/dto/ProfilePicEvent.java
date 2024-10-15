package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProfilePicEvent {
    private Long userId;
    private String profilePic;
    private LocalDateTime eventTime;
}
