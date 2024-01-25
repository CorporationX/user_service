package school.faang.user_service.dto.skill;

import lombok.Data;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.goal.Goal;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SkillDto {
    private long id;
    private String title;
    private List<User> users;
    private List<UserSkillGuarantee> guarantees;
    private List<EventDto> events;
    private List<Goal> goals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
