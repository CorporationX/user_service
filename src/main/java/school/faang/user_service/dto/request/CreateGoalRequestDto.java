package school.faang.user_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class CreateGoalRequestDto {

    private Long parentId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Long mentorId;
    private List<Long> userIds;
    private List<Long> skillsToAchieveIds;

}
