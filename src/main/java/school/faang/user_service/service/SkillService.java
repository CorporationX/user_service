package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillMapper skillMapper;
    private final int MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillMapper.toDto(skillRepository.save(skillMapper.toEntity(skill)));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().map(skillMapper::toDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> skillCandidateMapper.toDto(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        validateSkill(skillId, userId);
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            for (SkillOffer skillOffer : skillOffers) {
                long guarantorId = skillOffer.getRecommendation().getAuthor().getId();
                userSkillGuaranteeRepository.assignSkillGuaranteeToUser(skillId, userId, guarantorId);
            }
        }
        return skillMapper.toDto(skillRepository.getSkillById(skillId));
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle().isBlank() || skill.getTitle() == null) {
            throw new DataValidationException("title doesn't exist");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(skill.getTitle() + " already exist");
        }
    }

    private void validateSkill(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId) != null) {
            throw new DataValidationException("this skill with id " + skillId + " already exist");
        }
    }
}