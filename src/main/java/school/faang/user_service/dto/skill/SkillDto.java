package school.faang.user_service.dto.skill;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class SkillDto {
    private long id;
    private List<Long> userIds;
}
