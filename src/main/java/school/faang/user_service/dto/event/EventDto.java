package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.skill.SkillDto;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
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
