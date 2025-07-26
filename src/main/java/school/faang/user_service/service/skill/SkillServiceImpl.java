package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.skill_guarantee.UserSkillGuaranteeService;
import school.faang.user_service.validation.SkillValidator;

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
    private final SkillValidator skillValidator;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        boolean exists = skillRepository.existsByTitle(skillDto.title());
        skillValidator.validateSkillTitleIsUnique(exists, skillDto.title());
        Skill skill = skillMapper.toSkill(skillDto);
        skill = skillRepository.save(skill);
        log.info("Skill {} created", skill.getId());
        return skillMapper.toSkillDto(skill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skill -> {
                    List<UserDto> guarantors = getGuarantorsForSkill(skill, userId);
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
        boolean skillExists = skillRepository.existsById(skillId);
        skillValidator.ensureSkillExists(skillExists, skillId);
        boolean userHasSkill = skillRepository.existsUserSkill(skillId, userId);
        skillValidator.validateUserDoesNotHaveSkill(userHasSkill, skillId, userId);
        List<SkillOffer> offers = skillOfferService.getAllOffersOfSkill(skillId, userId);
        skillValidator.validateEnoughSkillOffers(offers);
        skillRepository.assignSkillToUser(skillId, userId);
        List<UserSkillGuarantee> userSkillGuarantees = userSkillGuaranteeMapper.toUserSkillGuarantees(offers);
        userSkillGuaranteeService.saveAll(userSkillGuarantees);
        log.info("Skill {} successfully assigned to user {}", skillId, userId);
    }

    private List<UserDto> getGuarantorsForSkill(Skill skill, Long userId) {
        if (skill.getGuarantees() == null) {
            return List.of();
        }
        return skill.getGuarantees().stream()
                .filter(userSkillGuarantee -> userSkillGuarantee.getUser().getId().equals(userId))
                .map(userSkillGuarantee -> userService.getUser(userSkillGuarantee.getGuarantor().getId()))
                .toList();
    }
}