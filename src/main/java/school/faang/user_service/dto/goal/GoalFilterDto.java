package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class GoalFilterDto {

    private String title;
    private RequestStatus status;
    private Long skillId;
}