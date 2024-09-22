package school.faang.user_service.dto.goal;

import lombok.Data;

@Data
public class GoalFilterDto {
    private String titlePattern;
    private String descriptionPattern;
}
