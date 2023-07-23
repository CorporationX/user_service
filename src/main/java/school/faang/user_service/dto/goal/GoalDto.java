package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

import java.util.List;

@Data
@RequiredArgsConstructor
public class GoalDto {
    private final Long id;
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, message = "Title must have at least 3 letters")
    private final String title;
    private List<User> users;
    private List<String> skills;
}
