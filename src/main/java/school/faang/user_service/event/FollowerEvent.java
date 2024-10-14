package school.faang.user_service.event;

import lombok.Data;

import java.io.Serializable;

@Data
public class FollowerEvent implements Serializable {
    private final Long followerId;
    private final Long followedId;
    private final String followerName;
}
