package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowerEvent {

    private Long followerId;
    private Long publisherId;
    private Long projectId;
    private LocalDateTime followedAt;
}
