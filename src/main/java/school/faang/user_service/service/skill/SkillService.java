package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.skill.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user_skill_guarantee.UserSkillGuaranteeService;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferService skillOfferService;
    private final UserService userService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final UserContext userContext;
    private final SkillValidator skillValidator;

    @Transactional(readOnly = true)
    public Skill getSkillByIdOrThrow(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    log.error("Skill with id {} not found", skillId);
                    return new SkillNotFoundException(skillId);
                });
    }

    @Transactional
    public void assignSkillsToUsers(final List<Long> skillIds, final List<Long> userIds) {
        skillIds.forEach(skillId ->
                userIds.forEach(userId ->
                        skillRepository.findUserSkill(skillId, userId)
                                .ifPresentOrElse(
                                        skill -> log.info(
                                                "User with id {} already has skill with id {}", userId, skillId),
                                        () -> {
                                            skillRepository.assignSkillToUser(skillId, userId);
                                            log.info("Assigned skill with id {} to user with id {}", skillId, userId);
                                        }
                                )
                )
        );
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsByUserId(long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsByIds(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    @Transactional
    public Skill create(Skill skill) {
        skillValidator.checkSkillTitleIsUnique(skill.getTitle());
        return skillRepository.save(skill);
    }

    @Transactional(readOnly = true)
    public List<Skill> getUserSkills() {
        long userId = userContext.getUserId();
        return skillRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Skill> getOfferedSkills() {
        long userId = userContext.getUserId();
        return skillRepository.findSkillsOfferedToUser(userId);
    }

    @Transactional
    public Skill acquireSkillFromOffers(long skillId) {
        long userId = userContext.getUserId();
        Skill skill = getSkillByIdOrThrow(skillId);
        return skillRepository.findUserSkill(userId, skillId)
                .orElseGet(() -> {
                    List<SkillOffer> offers = skillOfferService.findAllOffersOfSkill(skillId);
                    skillValidator.checkEnoughOffersToAcquireSkill(offers);
                    skillRepository.assignSkillToUser(skillId, userId);
                    List<UserSkillGuarantee> userSkillGuarantees = createGuaranteesFromOffers(offers, skill);
                    userSkillGuaranteeService.saveAll(userSkillGuarantees);
                    return skill;
                });
    }

    private List<UserSkillGuarantee> createGuaranteesFromOffers(List<SkillOffer> offers, Skill skill) {
        long userId = userContext.getUserId();
        User user = userService.getUserByIdOrThrow(userId);
        return offers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .user(user)
                        .skill(skill)
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build())
                .toList();
    }
}
