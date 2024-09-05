package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileViewEventDto {
    private long profileId;
    private long viewerId;
    private LocalDateTime viewTime;
}
