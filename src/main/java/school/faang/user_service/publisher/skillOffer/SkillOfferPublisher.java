package school.faang.user_service.publisher.skillOffer;

import school.faang.user_service.dto.skill.SkillOfferedEventDto;
import school.faang.user_service.publisher.MessagePublisher;

public interface SkillOfferPublisher extends MessagePublisher<SkillOfferedEventDto> {
}
