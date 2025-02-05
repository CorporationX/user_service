package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.rating.annotation.AcquireSkill;
import school.faang.user_service.service.rating.annotation.CreateSkill;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utility.validator.SkillValidator;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SkillService {

    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillValidator skillValidator;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillRepository skillRepository;

    public boolean skillExistsByTitle(String title) {
        return skillRepository.existsByTitle(title);
    }

    public void assignSkillToGoal(long skillId, long goalId) {
        skillRepository.assignSkillToGoal(skillId, goalId);
    }

    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    public void deleteSkill(Skill skill) {
        skillRepository.delete(skill);
    }

    public List<Skill> getSkills(List<Long> ids) {
        log.info("Getting Skills with ids {}", ids);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Skill> skills = skillRepository.findAllByIds(ids);
        if (skills.size() != ids.size()) {
            List<Long> missingSkillsIds = ids.stream()
                    .filter(id -> skills.stream()
                            .noneMatch(skill -> Objects.equals(skill.getId(), id)))
                    .toList();
            log.warn("Not found skills with ids {}", missingSkillsIds);
        }
        return skills;
    }

    @CreateSkill
    @Transactional
    public Skill create(Skill skill) {
        skillValidator.validateSkill(skill);

        return skillRepository.save(skill);
    }

    @Transactional
    public List<Skill> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skills;
    }

    @Transactional
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills
                .stream()
                .map(skill -> {
                    SkillCandidateDto dto =
                            skillCandidateMapper.toSkillCandidateDto(skill);
                    List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skill.getId(), userId);
                    dto.setOffersAmount(skillOffers.size());
                    return dto;
                })
                .toList();
    }

    @AcquireSkill
    @Transactional
    public Skill acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = getSkillById(skillId);
        User user = skillValidator.getUserById(userId);

        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        skillValidator.validateSkillOffers(skillOffers, skillId, userId);

        skillRepository.assignSkillToUser(skillId, userId);
        log.info("The skill {} was assigned to the user {} because it was received {} from {} offers",
                skill.getTitle(), user.getUsername(), skillOffers.size(), MIN_SKILL_OFFERS);
        userSkillGuaranteesSet(skillOffers, skillId, userId);
        skillRepository.save(skill);
        log.info("Updated the list of skill guarantors {} of the user {}", skill.getTitle(), user.getUsername());
        return skill;
    }

    private Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("Skill with ID %d not found", skillId)));
    }

    private List<UserSkillGuarantee> userSkillGuaranteesSet(List<SkillOffer> skillOffers, long skillId, long userId) {

        List<UserSkillGuarantee> userSkillGuarantees = skillOffers
                .stream()
                .map(skillOffer -> {
                    User guarantorUser = skillOffer.getRecommendation().getAuthor();
                    return new UserSkillGuarantee(null, skillValidator.getUserById(userId), getSkillById(skillId)
                            , guarantorUser);
                })
                .toList();
        getSkillById(skillId).setGuarantees(userSkillGuarantees);
        return userSkillGuarantees;
    }
}
