package school.faang.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectSubscriptionDto {
    private long id;
    private long followerId;
    private long projectId;
}
