package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FollowerEventDto {
    private long followerId;
    private long followeeId;
    private LocalDateTime eventTime;
}