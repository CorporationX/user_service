package school.faang.user_service.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectSubscriptionDto {
    private long id;
    private long followerId;
    private long projectId;
}
