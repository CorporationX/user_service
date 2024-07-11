package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.dto.event.EventDto;

import java.util.List;

@Data
@AllArgsConstructor
public class SkillDto {
    private Long id;
    private String title;
    private List<EventDto> events;
}
