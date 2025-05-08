package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class SkillRequestDto {
    private long id;
    private Long requestId;
    private Long skillId;
}
