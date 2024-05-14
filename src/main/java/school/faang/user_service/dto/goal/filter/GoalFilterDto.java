package school.faang.user_service.dto.goal.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private String statusPattern;
}
