package school.faang.user_service.model.dto.recommendation;

import lombok.Builder;

@Builder
public record SkillOfferDto(Long id, Long skillId) {
}
