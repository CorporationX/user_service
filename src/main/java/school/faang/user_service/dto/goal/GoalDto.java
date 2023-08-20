package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GoalDto {
    private final Long id;
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, message = "Title must have at least 3 letters")
    private final String title;
    private String description;
    private Long parentId;
    private GoalStatus status;
    private List<Long> skillIds;
    private List<Long> userIds;
    private List<String> skills;
}