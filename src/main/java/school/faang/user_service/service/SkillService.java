package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;


import java.util.List;

@Component
public class SkillService {
    @Autowired
    SkillRepository skillRepository;
    @Autowired
    private SkillMapper skillMapper;
    @Autowired
    private SkillOfferRepository skillOfferRepository;

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        List<SkillDto> skillsDtos = skills.stream().map(skill -> skillMapper.skillToSkillDto(skill)).toList();
        return skillsDtos;
    }

    public SkillDto create(SkillDto skill) throws IllegalAccessException {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill skillDto = new Skill();
            skillDto.setTitle(skill.getTitle());
            skillDto.setId(skill.getUserId());
            Skill savedSkill = skillRepository.save(skillDto);
            SkillDto createdSkillDto = skillMapper.skillToSkillDto(savedSkill);
            return createdSkillDto;
        } else {
            throw new IllegalAccessException("This skill already exists.");
        }
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillCandidate = skillRepository.findSkillsOfferedToUser(userId);
        List<SkillCandidateDto> skillCandidateDtoList = skillCandidate.stream().map(skill -> skillMapper.skillToSkillCandidateDto(skill)).toList();
        return skillCandidateDtoList;
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) throws IllegalAccessException {
        Skill skill = skillRepository.findById(skillId).get();
        SkillDto skillDto = skillMapper.skillToSkillDto(skill);
        if (skillRepository.findUserSkill(skillId, userId).isEmpty()) {
            List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (skillOffers.size() >= 3) {
                skillRepository.assignSkillToUser(skillId, userId);
                for (SkillOffer skillOffer : skillOffers) {
                    skillRepository.assignGuarantorToUserSkill(userId, skillId, skillOffer.getId());
                }
            }
            return skillDto;
        } else {
            throw new IllegalArgumentException("This skill already exists.");
        }
    }
}
