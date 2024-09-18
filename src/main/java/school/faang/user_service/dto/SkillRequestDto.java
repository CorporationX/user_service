package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record SkillRequestDto (
    Long id,
    Long requestId,
    Long skillId
) {}
