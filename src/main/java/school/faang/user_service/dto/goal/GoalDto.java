package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoalDto {
    private Long id;      //ай ди цели
    private Long parentId;      // ID предка
    private String title;    // название цели
    private GoalStatus status;
    private String description; //Описание цели
    private List<Long> skillIds;   //Список ID навыков, связанных с этой целью
    private LocalDateTime deadline; //Дедлайн выполнения цели
    private List<Long> usersId;
}
