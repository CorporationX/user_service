package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@Builder
public class EventFilterDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private String description;
    private List<SkillDto> relatedSkills;
    private String location;
    private int maxAttendees;
}
