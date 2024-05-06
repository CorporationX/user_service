package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

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
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillMapper skillMapper;
    private final int MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        skillValidator.validateSkill(skill);
        Skill convertedSkill = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(convertedSkill));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().map(skillMapper::toDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> skillCandidateMapper.toDto(entry.getKey(), entry.getValue())).toList();
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
    }
