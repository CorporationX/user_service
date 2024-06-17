package school.faang.user_service.service.skill;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.message.ExceptionMessage;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static school.faang.user_service.exception.message.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;

    @Transactional
    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateSkill(skillDto);
        Skill skillEntity = skillMapper.dtoToSkill(skillDto);
        return skillMapper.skillToDto(skillRepository.save(skillEntity));
    }

    @Transactional
    public List<SkillDto> getUserSkills(long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new DataValidationException(NO_SUCH_USER_EXCEPTION.getMessage());
        }

        List<Skill> ownerSkillsList = skillRepository.findAllByUserId(userId);
        return skillMapper.skillToDto(ownerSkillsList);
    }

    @Transactional
    public List<SkillCandidateDto> getOfferedSkills(long userId) {

        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        Map<Skill, Long> skillCountMap = skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return skillCountMap.entrySet().stream()
                .map(entry -> SkillCandidateDto.builder()
                        .skillDto(skillMapper.skillToDto(entry.getKey()))
                        .offersAmount(entry.getValue())
                        .build())
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        skillValidator.validateSkillPresent(skillId, userId);

        Skill skillUser = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new ValidationException(ExceptionMessage.USER_SKILL_NOT_FOUND.getMessage()));

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        skillValidator.validateMinSkillOffers(skillOffers.size(), skillId, userId);

        skillRepository.assignSkillToUser(skillId, userId);
        addUserSkillGuarantee(skillUser, skillOffers);

        return skillMapper.skillToDto(skillUser);
    }


    private void addUserSkillGuarantee(Skill userSkill, List<SkillOffer> allOffersOfSkill) {
        allOffersOfSkill.stream()
                .map(skillOffer -> UserSkillGuarantee.builder()
                        .user(skillOffer.getRecommendation().getReceiver())
                        .skill(userSkill)
                        .guarantor(skillOffer.getRecommendation().getAuthor())
                        .build())
                .forEach(userSkillGuaranteeRepository::save);
    }

    @Transactional
    public boolean skillExistsByTitle(String title) {
        return skillRepository.existsByTitle(title);
    }

    @Transactional
    public Optional<Skill> findUserSkill(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId);
    }
}


