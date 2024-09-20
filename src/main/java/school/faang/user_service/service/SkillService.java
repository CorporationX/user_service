package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validation.SkillOfferValidator;
import school.faang.user_service.validation.SkillValidator;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final UserService userService;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillValidator;
    private final SkillOfferValidator skillOfferValidator;


    public SkillDto create(SkillDto skill) {
        skillValidator.validateSkill(skill);
        Skill skillToAdd = skillMapper.toEntity(skill);
        skillRepository.save(skillToAdd);
        return skillMapper.toDto(skillToAdd);
    }

    public List<SkillDto> getUserSkills(long userId) {
        userService.getUser(userId);
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toSkillDtoList(skills);
    }

    public Skill getSkill(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Skill with id " + skillId + " doesn't exist"));
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = getSkill(skillId);
        User user = userService.getUser(userId);
        skillValidator.checkUserSkill(skillId, userId);
        List<SkillOffer> offers = skillOfferService.findAllOffersOfSkill(skill, user);
        skillOfferValidator.validateOffers(offers, skill, user);
        skillRepository.assignSkillToUser(skillId, userId);
        List<UserSkillGuarantee> guaranteeList = new ArrayList<>();
        for (SkillOffer offer : offers) {
            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setSkill(skill);
            guarantee.setUser(user);
            guarantee.setGuarantor(offer.getRecommendation().getAuthor());
            guaranteeList.add(guarantee);
        }
        userSkillGuaranteeService.saveAll(guaranteeList);
        return skillMapper.toDto(skill);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        List<SkillDto> skillsDto = skillMapper.toSkillDtoList(skills);
        return skillCandidateMapper.toSkillCandidateDtoList(skillsDto);
    }
}
