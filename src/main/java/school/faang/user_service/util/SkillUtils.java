package school.faang.user_service.util;

import school.faang.user_service.model.Skill;
import school.faang.user_service.model.recommendation.Recommendation;
import school.faang.user_service.model.recommendation.SkillOffer;

import java.util.List;

public class SkillUtils {

    public static List<Skill> toSkillList(List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map(SkillOffer::getSkill)
                .toList();
    }

    public static List<Skill> getSkillsFromRecommendations(List<Recommendation> otherRecommendation) {
        return otherRecommendation.stream()
                .flatMap(currRecommendation -> currRecommendation.getSkillOffers().stream())
                .map(SkillOffer::getSkill)
                .toList();
    }
}
