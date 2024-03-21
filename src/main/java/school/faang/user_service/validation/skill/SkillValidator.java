package school.faang.user_service.validation.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void validateSkill(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title \"" + skillDto.getTitle() + "\" already exists");
        }
    }

    public void validateSkillTitle(SkillDto skillDto) {
        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Skill title can't be empty");
        }
    }

    public void validateSupplyQuantityCheck(Long skillId, Long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User already has the skill");
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("You need at least 3 recommendations, at the moment you have: "
                    + skillOffers.size());
        }
    }
}