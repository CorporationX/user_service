package school.faang.user_service.service.skillService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.skill.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService {

    private final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserValidator userValidator;
    private final SkillValidator skillValidation;
    private final SkillMapper skillMapper;


    @Override
    @Transactional
    public SkillDto create(SkillDto skill) {
        if (!skillValidation.titleIsValid(skill.getTitle())) {
            throw new DataValidationException("Skill title can't be empty");
        }

        if (skillValidation.existByTitle(skill.getTitle())) {
            throw new DataValidationException(String.format("Skill with title %s has already exist", skill.getTitle()));
        }

        Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));

        return skillMapper.toDto(savedSkill);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SkillDto> getUsersSkills(Long userId) {
        if (skillValidation.isNullableId(userId)) {
            throw new IllegalArgumentException("Id can't be empty");
        }

        if (!userValidator.doesUserExistsById(userId)) {
            throw new DataValidationException(String.format("User with %s id doesn't exist", userId));
        }

        List<Skill> skillList = skillRepository.findAllByUserId(userId);
        return skillMapper.toSkillDtoList(skillList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Id can't be nullable");
        }

        if (!userValidator.doesUserExistsById(userId)) {
            throw new DataValidationException(String.format("User with %s id doesn't exist", userId));
        }

        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        return skillMapper.toSkillCandidateDtoList(skillsOfferedToUser);
    }

    @Transactional
    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (!userValidator.doesUserExistsById(userId)) {
            throw new DataValidationException(String.format("User with %s id doesn't exist", userId));
        }

        Optional<Skill> skill = skillRepository.findById(skillId);

        if (skill.isEmpty()) {
            throw new DataValidationException(String.format("Skill with %s doesn't exist", skillId));
        }

        Optional<Skill> userSkillOptional = skillRepository.findUserSkill(skillId, userId);

        if (userSkillOptional.isPresent()) {
            throw new DataValidationException(String.format("User %s already has %s skill", userId, skillId));
        }

        List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);

            List<UserSkillGuarantee> userSkillGuarantees = allOffersOfSkill.stream()
                    .map(offer -> UserSkillGuarantee.builder()
                            .skill(offer.getSkill())
                            .guarantor(offer.getRecommendation().getAuthor())
                            .user(offer.getRecommendation().getReceiver())
                            .build())
                    .distinct()
                    .toList();

            userSkillGuaranteeRepository.saveAll(userSkillGuarantees);

            return skillMapper.toSkillDto(skill.get());
        } else {
            throw new DataValidationException("User doesn't have enough offers to acquire skill");
        }
    }
}
