package school.faang.user_service.service.skill_offer;

import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

public interface SkillOfferService {

    List<SkillOffer> getAllOffersOfSkill(long skillId, long userId);

    int countAllOffersOfSkill(long skillId, long userId);
}
