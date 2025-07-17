package school.faang.user_service.dto.goal;

import lombok.Data;

import java.util.List;

@Data
public class GoalDto implements GoalRequest {
    private Long id;
    private String status;
    private String title;
    private String description;
    private String deadline;
    private Long mentorId;
    private List<Long> skillIds;
    private List<GoalDto> subGoals;
}
