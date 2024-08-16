package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.ArrayList;
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
        skillValidator.validateSkillDto(skill);
        Skill skillEntity = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().
                map(skill -> skillMapper.toDto(skill)).collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skillCandidateMapper.toListDto(skills);
    }

    public Skill findById(long skillId) {
        return skillRepository.findById(skillId).orElseThrow(() -> new DataValidationException("the skill is not found"));
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = findById(skillId);
        Optional<Skill> offeredSkill = skillRepository.findUserSkill(skillId, userId);
        if (offeredSkill.isEmpty()) {
            if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) >= MIN_SKILL_OFFERS) {
                addSkillGuarantors(skillId, userId, skill);
                skillRepository.assignSkillToUser(skillId, userId);
            }
        }
        return skillMapper.toDto(skill);
    }

    private void addSkillGuarantors(long skillId, long userId, Skill skill) {
        skillOfferRepository.findAllOffersOfSkill(skillId, userId).forEach(skillOffer -> {
            User receiver = skillOffer.getRecommendation().getReceiver();
            User author = skillOffer.getRecommendation().getAuthor();
            UserSkillGuarantee guarantee = userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                    .user(receiver).guarantor(author).skill(skillOffer.getSkill()).build());
            if (skill.getGuarantees() == null) {
                skill.setGuarantees(new ArrayList<>());
            }
            skill.getGuarantees().add(guarantee);
        });
    }

    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public int countExisting(List<Long> ids) {
        return skillRepository.countExisting(ids);
    }

}
