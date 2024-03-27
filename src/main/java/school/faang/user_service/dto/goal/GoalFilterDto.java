package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalFilterDto {

    @Size(max = 255, message = "Goal title pattern can't be longer than 255 characters")
    private String titlePattern;
    private GoalStatus statusPattern;
    @Size(max = 255, message = "Goal description pattern can't be longer than 255 characters")
    private String descriptionPattern;
    private List<Long> skillIds;
}