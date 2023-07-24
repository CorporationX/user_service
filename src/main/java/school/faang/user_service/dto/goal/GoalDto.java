package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

import java.util.List;

@Data
@RequiredArgsConstructor
public class GoalDto {
    private final Long id;
    private final String title;
    private List<User> users;
    private List<String> skills;
}
