package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.dto.goal.GoalDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
    private List<GoalDto> goalIds;

    public List<GoalDto> getGoals() {
        return goalIds;
    }
    public void setGoals(List<GoalDto> goalIds) {
        this.goalIds = goalIds;
    }

    public void addGoals(GoalDto goalDto) {
        if (goalIds == null) {
            goalIds = new ArrayList<>();
        }
        goalIds.add(goalDto);
    }




}
