package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponseDto {
    private Long id;
    private Long parentGoalId;
    private String title;
    private String description;
    private GoalStatus status;
    private List<Long> userIds;
    private List<Long> skillIds;
}
