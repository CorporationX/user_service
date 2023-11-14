package school.faang.user_service.dto.goal;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class GoalDto {
    private Long id;
    private String description;
    private Long parentId;
    private String title;
    private GoalStatus status;
    private List<Long> skillIds;
}