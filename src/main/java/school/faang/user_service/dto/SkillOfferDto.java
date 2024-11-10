package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record SkillOfferDto (
    Long id,
    Long skillId
) {
}
