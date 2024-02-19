package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {
    @NotNull(message = "Поле id не может быть пустым")
    @Positive(message = "Id должен быть положительным и больше 0")
    private Long id;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 255, message = "Описание не должно превышать 255 символов")
    private String description;
    @Positive(message = "parentId должен быть положительным числом")
    private Long parentId;
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;
    @NotNull(message = "Status cannot be null")
    private GoalStatus status;
    private List<Long> skillIds;
}
