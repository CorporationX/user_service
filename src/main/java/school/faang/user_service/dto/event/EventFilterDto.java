package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventFilterDto {
    private String titlePattern;
    private Long ownerId;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
    private List<SkillDto> skills;
    private EventType eventType;
}
