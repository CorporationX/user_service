package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final long MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        skillValidator.validateSkill(skill);
        Skill skillEntity = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().
                map(skill -> skillMapper.toDto(skill)).collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        skillValidator.validateUserSkills(skills);
        return skillCandidateMapper.toListDto(skills);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> skill = skillRepository.findUserSkill(skillId, userId);
            List<SkillOffer> offersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (offersOfSkill.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                addGuarantee(offersOfSkill);
                return getSkillById(skillId);
            }
        return skillMapper.toDto(skill.get());
    }

    public void addGuarantee(List<SkillOffer> skillOffers) {
        skillValidator.validateSkillOffers(skillOffers);
        for (SkillOffer skillOffer : skillOffers) {
            User receiver = skillOffer.getRecommendation().getReceiver();
            User author = skillOffer.getRecommendation().getAuthor();
            userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                    .user(receiver).guarantor(author).skill(skillOffer.getSkill()).build());
        }
    }

    public SkillDto getSkillById(long skillId) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new DataValidationException("the skill is not found"));
        return skillMapper.toDto(skill);
    }

}
