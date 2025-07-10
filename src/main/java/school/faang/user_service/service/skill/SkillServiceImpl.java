package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.skill_guarantee.UserSkillGuaranteeService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final SkillMapper skillMapper;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final UserService userService;

    @Value("${skill.min-offers-required}")
    private int minOffersRequired;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        validateSkillTitleIsUnique(skillDto.title());
        Skill skill = skillMapper.toSkill(skillDto);
        skill = skillRepository.save(skill);
        log.info("Skill {} created", skill.getId());
        return skillMapper.toSkillDto(skill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skill -> {
                    List<UserDto> guarantors = skill.getGuarantees().stream()
                            .filter(userSkillGuarantee -> userSkillGuarantee.getUser().getId().equals(userId))
                            .map(userSkillGuarantee -> userService.getById(userSkillGuarantee.getGuarantor().getId()))
                            .toList();
                    return skillMapper.toSkillDtoWithGuarantors(skill, guarantors);
                })
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skillMapper.toSkillCandidateDtos(
                skills,
                skill -> skillOfferService.countAllOffersOfSkill(skill.getId(), userId)
        );
    }


    @Transactional
    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        ensureSkillExists(skillId);
        validateUserDoesNotHaveSkill(skillId, userId);
        List<SkillOffer> offers = skillOfferService.getAllOffersOfSkill(skillId, userId);
        validateEnoughSkillOffers(offers);
        skillRepository.assignSkillToUser(skillId, userId);
        List<UserSkillGuarantee> userSkillGuarantees = userSkillGuaranteeMapper.toUserSkillGuarantees(offers);
        userSkillGuaranteeService.saveAll(userSkillGuarantees);
        log.info("Skill {} successfully assigned to user {}", skillId, userId);
    }

    private void validateSkillTitleIsUnique(String title) {
        if (skillRepository.existsByTitle(title)) {
            throw new DataValidationException("Skill with title: " + title + " already exists");
        }
    }

    private void ensureSkillExists(long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException("Skill with id " + skillId + " does not exist");
        }
    }

    private void validateUserDoesNotHaveSkill(long skillId, long userId) {
        skillRepository.findUserSkill(skillId, userId)
                .ifPresent(skill -> {
                    throw new ForbiddenException("User already has this skill.");
                });
    }


    private void validateEnoughSkillOffers(List<SkillOffer> offers) {
        long uniqueAuthorsCount = offers.stream()
                .map(offer -> offer.getRecommendation().getAuthor().getId())
                .distinct()
                .count();
        if (uniqueAuthorsCount < minOffersRequired) {
            throw new ForbiddenException("Skill cannot be acquired. At least " + minOffersRequired
                    + " unique users must offer this skill."
            );
        }
    }
}