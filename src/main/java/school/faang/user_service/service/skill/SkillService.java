package school.faang.user_service.service.skill;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.rating.annotation.RatingChanging;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static school.faang.user_service.utils.skill.SkillErrorMessage.NOT_ENOUGH_SKILL_OFFERS;
import static school.faang.user_service.utils.skill.SkillErrorMessage.SKILL_ALREADY_EXISTS;
import static school.faang.user_service.utils.skill.SkillErrorMessage.USER_ALREADY_HAS_SKILL;

@RequiredArgsConstructor
@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final int minSkillOffers = 3;

    public Optional<Skill> findSkillById(Long skillId) {
        return skillRepository.findById(skillId);
    }

    @Transactional
    public void assignSkillsFromGoalToUsers(Long goalId, List<User> userIds) {
        if (userIds == null) {
            return;
        }
        skillRepository.findSkillsByGoalId(goalId).forEach(
                skill -> userIds.forEach(user -> assignSkillToUser(user.getId(), goalId))
        );
    }

    @RatingChanging(ratingType = RatingType.SKILL_RATING)
    private void assignSkillToUser(Long userId, Long skillId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    @Transactional
    public Skill create(Skill skill){
    if (skillRepository.existsByTitle(skill.getTitle())) {
        throw new DataValidationException(SKILL_ALREADY_EXISTS);
    }

    return skillRepository.save(skill);
}

    @Transactional(readOnly = true)
    public List<Skill> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Skill> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId);
    }

    @RatingChanging(ratingType = RatingType.SKILL_RATING)
    @Transactional
    public Skill acquireSkillFromOffers(long userId, long skillId) {
        skillRepository.findUserSkill(skillId, userId)
                .ifPresent((skill) -> {
                    throw new DataValidationException(
                            String.format(USER_ALREADY_HAS_SKILL, skillId));
                });

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(
                skillId, userId);
        if (skillOffers.size() >= minSkillOffers) {
            List<UserSkillGuarantee> guarantees = skillOffers.stream()
                    .map(skillOffer -> UserSkillGuarantee.builder()
                            .guarantor(skillOffer.getRecommendation().getAuthor())
                            .user(skillOffer.getRecommendation().getReceiver())
                            .skill(skillOffer.getSkill())
                            .build()
                    ).toList();
            userSkillGuaranteeRepository.saveAll(guarantees);
            skillRepository.assignSkillToUser(skillId, userId);
        } else {
            throw new DataValidationException(NOT_ENOUGH_SKILL_OFFERS);
        }
        return skillRepository.findUserSkill(skillId, userId).get();
    }

    public Map<Long, Integer> getNumberOfSkillsPerUser() {
        return skillRepository.countAssignedSkillsPerUser();
    }
}
