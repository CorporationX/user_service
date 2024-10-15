package school.faang.user_service.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowEvent {
    private Long followeeId;
    private Long followerId;
    private LocalDateTime followedAt;
}
