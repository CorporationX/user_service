package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class GoalDto {
    private Long id;
    private Long parentId;
    private String title;
    private String description;
    private GoalStatus status;
    private List<Long> skillIds;
}
