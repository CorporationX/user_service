package school.faang.user_service.dto.user;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import java.util.List;
@Data
@Component
public class UserDto {
    Long id;
    List<SkillDto> skills;
    List<EventDto> events;
}
