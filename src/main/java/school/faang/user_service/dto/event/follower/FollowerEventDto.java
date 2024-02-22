package school.faang.user_service.dto.event.follower;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerEventDto {
    private long followerId;
    private long followeeId;
    private LocalDateTime subscriptionTime;
}
