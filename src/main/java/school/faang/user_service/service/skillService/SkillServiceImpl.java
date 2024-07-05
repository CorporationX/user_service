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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService, SkillValidation, UserValidation {

    private final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private final UserRepository userRepository;
    private final SkillMapper skillMapper;


    @Override
    @Transactional
    public SkillDto create(SkillDto skill) {
        if (!titleIsValid(skill.getTitle())) {
            throw new DataValidationException("Skill title can't be empty");
        }

        if (existByTitle(skill.getTitle())) {
            throw new DataValidationException(String.format("Skill with %s doesn't exist", skill.getId()));
        }

        Skill savedSkill = skillRepository.save(skillMapper.toSkill(skill));

        return skillMapper.toSkillDto(savedSkill);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SkillDto> getUsersSkills(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Id can't be nullable");
        }

        isUserExistsById(userId);

        List<Skill> skillList = skillRepository.findAllByUserId(userId);
        return skillMapper.toSkillDtoList(skillList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Id can't be nullable");
        }

        isUserExistsById(userId);

        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        return skillMapper.toSkillCandidateDtoList(skillsOfferedToUser);
    }

    @Transactional
    @Override
    public Optional<SkillDto> acquireSkillFromOffers(long skillId, long userId) {
        isUserExistsById(userId);

        Optional<Skill> skill = skillRepository.findById(skillId);

        if (!skill.isPresent()) {
            throw new DataValidationException(String.format("Skill with %s doesn't exist", skillId));
        }

        Optional<Skill> userSkillOptional = skillRepository.findUserSkill(skillId, userId);

        if (userSkillOptional.isPresent()) {
            log.info("User {} already has {} skill", userId, skillId);
            return Optional.empty();
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

            return Optional.of(skillMapper.toSkillDto(skill.get()));
        } else {
            log.info("User doesn't have enough offers to acquire skill");
            return Optional.empty();
        }
    }

    @Override
    public boolean titleIsValid(String title) {
        return !title.isBlank();
    }

    @Override
    public boolean existByTitle(String title) {
        return skillRepository.existsByTitle(title);
    }

    @Override
    public void isUserExistsById(long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException(String.format("User with %s id doesn't exist", id));
        }
    }
}
