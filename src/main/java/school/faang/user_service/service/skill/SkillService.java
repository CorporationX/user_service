package school.faang.user_service.service.skill;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.message.SkillAcquiredEventMessage;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.UserSkillGuaranteeService;
import school.faang.user_service.validator.skill.SkillOfferValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final UserService userService;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillValidator;
    private final SkillOfferValidator skillOfferValidator;
    private final SkillAcquiredEventPublisher publisher;

    public SkillDto create(SkillDto skill) {
        skillValidator.validateSkill(skill);
        Skill skillToAdd = skillMapper.toEntity(skill);
        skillRepository.save(skillToAdd);
        return skillMapper.toDto(skillToAdd);
    }

    public List<SkillDto> getUserSkills(long userId) {
        userService.getUserById(userId);
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toSkillDtoList(skills);
    }

    public Skill getSkill(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Skill with id " + skillId + " doesn't exist"));
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = getSkill(skillId);
        User user = userService.getUserById(userId);
        skillValidator.checkUserSkill(skillId, userId);
        List<SkillOffer> offers = skillOfferService.findAllOffersOfSkill(skill, user);
        skillOfferValidator.validateOffers(offers, skill, user);
        skillRepository.assignSkillToUser(skillId, userId);

        SkillAcquiredEventMessage message = createMessage(skill, userId);
        publisher.publish(message);


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

    public List<Skill> getSkillByIds(List<Long> ids) {
        if (ids == null) {
            throw new DataValidationException("List ids cannot be null");
        }

        return skillRepository.findByIdIn(ids);
    }

    public void saveSkill(@Valid Skill skill) {
        skillRepository.save(skill);
    }

    public List<Skill> getAllSkills(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    private SkillAcquiredEventMessage createMessage(Skill skill, long userId) {
        return SkillAcquiredEventMessage.builder()
                .skillId(skill.getId())
                .receiverId(userId)
                .skillTitle(skill.getTitle())
                .build();
    }
}
