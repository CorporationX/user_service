package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowerEvent {
    private Long followerId;
    private Long followedId;
    private String followerName;
}
