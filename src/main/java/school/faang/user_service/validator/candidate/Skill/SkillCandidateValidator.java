package school.faang.user_service.validator.candidate.Skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillCandidateValidator {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void validateSkillOfferSize(long skillId, long userId) throws IllegalArgumentException {
        final int requiredSize = 3;
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() >= requiredSize) {
            skillRepository.assignSkillToUser(skillId, userId);
            for (SkillOffer skillOffer : skillOffers) {
                skillRepository.assignGuarantorToUserSkill(userId, skillId, skillOffer.getId());
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}