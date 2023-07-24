package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private final String title;
    private String description;
    private Long parentId;
    private GoalStatus status;
    private List<Long> skillIds;
    private List<User> users;
    private List<String> skills;
}
