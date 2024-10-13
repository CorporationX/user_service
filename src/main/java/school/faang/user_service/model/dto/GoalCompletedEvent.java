package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletedEvent implements Serializable {
    private Long userId;
    private Long goalId;
}
