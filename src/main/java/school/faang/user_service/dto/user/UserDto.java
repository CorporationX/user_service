package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private List<Goal> goals;
}
