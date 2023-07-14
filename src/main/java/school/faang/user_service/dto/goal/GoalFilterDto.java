package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GoalFilterDto {
    private String title; // Фильтр по названию цели
    private GoalStatus status; // Фильтр по статусу цели
    private List<Long> skillIds; // Фильтр по идентификаторам навыков
    private Long parentId; // Фильтр по идентификатору родительской цели
}
