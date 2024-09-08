package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillValidator;


    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream().map(skill -> skillMapper.skillToSkillDto(skill)).toList();
    }

    public SkillDto create(SkillDto skill) throws IllegalArgumentException {
        skillValidator.validateSkill(skill);
        Skill skillEntity = skillMapper.skillDtoToSkill(skill);
        return skillMapper.skillToSkillDto(skillRepository.save(skillEntity));
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillCandidate = skillRepository.findSkillsOfferedToUser(userId);
        List<SkillCandidateDto> skillCandidateDtoList = skillCandidate.stream()
                .map(skill -> skillCandidateMapper.skillToSkillCandidateDto(skill)).toList();
        return skillCandidateDtoList;
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) throws IllegalArgumentException {
        SkillDto skillDto = skillRepository.findById(skillId)
                .map(skillMapper::skillToSkillDto)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        skillValidator.validateSkill(skillDto);
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() >= 3) { //не придумал как сообразить здесь валидацию в отдельном методе класса
            skillRepository.assignSkillToUser(skillId, userId);
            for (SkillOffer skillOffer : skillOffers) {
                skillRepository.assignGuarantorToUserSkill(userId, skillId, skillOffer.getId());
            }
        }
        return skillDto;

    }
}
