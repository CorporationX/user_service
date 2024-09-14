package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;

import school.faang.user_service.exceptions.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.candidate.skill.SkillCandidateValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillValidator;
    private final SkillCandidateValidator skillCandidateValidator;
    private final SkillOfferRepository skillOfferRepository;

    @Override
    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toDtoList(skills);
    }

    @Override
    public SkillDto create(Skill skill) throws DataValidationException {
        boolean existByTitle = existByTitle(skill);
        skillValidator.validateSkill(skill, existByTitle);
        skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillCandidates = skillRepository.findSkillsOfferedToUser(userId);
        return skillCandidateMapper.toListDto(skillCandidates);
    }

    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) throws DataValidationException {
        Skill skillFromOffers = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new DataValidationException("Skill not found"));
        boolean existByTitle = existByTitle(skillFromOffers);
        skillValidator.validateSkill(skillFromOffers, existByTitle);
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        skillCandidateValidator.validateSkillOfferSize(skillOffers);
        skillRepository.assignSkillToUser(skillId, userId);
        for (SkillOffer skillOffer : skillOffers) {
            skillRepository.assignGuarantorToUserSkill(userId, skillId, skillOffer.getId());
            return skillMapper.toDto(skillFromOffers);
        }
        return skillMapper.toDto(skillFromOffers);
    }
        private boolean existByTitle (Skill skill){
            return skillRepository.existsByTitle(skill.getTitle());
        }
    }
