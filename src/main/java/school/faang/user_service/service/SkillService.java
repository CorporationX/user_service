package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public Skill createSkill(Skill skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill with this title already exists!");
        }
        return skillRepository.save(skill);
    }

    public List<Skill> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills;
    }

    public List<Skill> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        return offeredSkills;
    }

    public Skill acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(
                () -> new DataValidationException("Skill with ID " + skillId + " not found"));

        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User already has this skill");
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough offers to acquire the skill");
        }
        skillRepository.assignSkillToUser(skillId, userId);

        List<UserSkillGuarantee> userSkillGuarantees = skillOffers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .skill(skill)
                        .guarantor(offer.getRecommendation().getAuthor())
                        .user(offer.getRecommendation().getReceiver())
                        .build())
                .distinct()
                .toList();

        userSkillGuaranteeRepository.saveAll(userSkillGuarantees);

        return skill;
    }
}
