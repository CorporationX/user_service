package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserValidator userValidator;

    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateSkill(skillDto);
        Skill skillEntity = skillMapper.toEntity(skillDto);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(long userId) {
        userValidator.validateUserExistsById(userId);
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toDto(skills);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        userValidator.validateUserExistsById(userId);
        return skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        skillValidator.validateSupplyQuantityCheck(skillId, userId);

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        Skill skillUser = skillRepository.findUserSkill(skillId, userId).orElse(null);

        skillRepository.assignSkillToUser(skillId, userId);
        addUserSkillGuarantee(skillUser, skillOffers);
        return skillMapper.toDto(skillUser);
    }

    private void addUserSkillGuarantee(Skill userSkill, List<SkillOffer> allOffersOfSkill) {
        for (SkillOffer skillOffer : allOffersOfSkill) {
            userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                    .user(skillOffer.getRecommendation().getReceiver())
                    .skill(userSkill)
                    .guarantor(skillOffer.getRecommendation().getAuthor())
                    .build());
        }
    }
}