package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
    private List<Long> goalIds;
    //private List<Goal> goalIds;



}
