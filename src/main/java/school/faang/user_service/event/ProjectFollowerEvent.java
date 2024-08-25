package school.faang.user_service.event;

import lombok.Data;

@Data
public class ProjectFollowerEvent {
    private long projectId;
    private long followerId;
    private long authorId;
}
