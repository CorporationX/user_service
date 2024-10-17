package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserFollowerEvent {
    private Long followerId;
    private Long followedUserId;
}
