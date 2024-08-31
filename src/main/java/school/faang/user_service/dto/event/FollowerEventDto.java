package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FollowerEventDto {

    private Long visitorId;
    private Long visitedId;
    private Long projectId;
    private LocalDateTime subscribedDateTime;
}
