package school.faang.user_service.validator.candidate.Skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillCandidateValidator {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillOfferMapper skillOfferMapper;

    public void validateCandidateSkill(SkillDto skillDto) throws IllegalArgumentException {
        if (skillDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Validation failed. Skill name is blank");
        }
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new IllegalArgumentException("This skill already exists.");
        }
    }

    public void validateSkillOfferSize(long skillId, long userId) throws IllegalArgumentException {
        List<Long> skillOffers = skillOfferMapper.toListIds(skillOfferRepository.findAllOffersOfSkill(skillId, userId));
        if (skillOffers.size() >= 3) {
            skillRepository.assignSkillToUser(skillId, userId);
            for (Long skillOfferIds : skillOffers) {
                skillRepository.assignGuarantorToUserSkill(userId, skillId, skillOfferIds);
            }
        }
    }
}