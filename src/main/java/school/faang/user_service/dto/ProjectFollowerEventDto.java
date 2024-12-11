package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectFollowerEventDto {
    private Long projectId;
    private Long followerId;
    private Long creatorId;

    public enum EventType {
        FOLLOW, UNFOLLOW
    }
}
