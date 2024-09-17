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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private static final int MINIMUM_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        Skill skillToAdd = skillMapper.toEntity(skill);
        skillRepository.save(skillToAdd);
        return skillMapper.toDto(skillToAdd);
    }

    private void validateSkill(SkillDto skill) {
        if (skill == null) {
            throw new DataValidationException("DTO = null");
        }
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Название скила не должно быть пустым");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Такой скилл уже существует");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        getValidUser(userId);
        List<SkillDto> skills = skillRepository.findAllByUserId(userId).stream().map(skillMapper::toDto).toList();
        if (!skills.isEmpty()) {
            return skills;
        } else {
            throw new DataValidationException("У пользователя нет умений");
        }
    }

    Skill getValidSkill(long skillId) {
        Optional<Skill> skill = skillRepository.findById(skillId);
        if (skill.isEmpty()) {
            throw new DataValidationException("Указанный скилл не существует");
        } else {
            return skill.get();
        }
    }

    User getValidUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataValidationException("Указанный пользователь не существует");
        } else {
            return user.get();
        }
    }

    void checkUserSkill(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("У пользователя уже есть данный скилл");
        }
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = getValidSkill(skillId);
        User user = getValidUser(userId);
        checkUserSkill(skillId, userId);
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        validateOffers(offers);
        skillRepository.assignSkillToUser(skillId, userId);
        for (SkillOffer offer : offers) {
            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setSkill(skill);
            guarantee.setUser(user);
            guarantee.setGuarantor(offer.getRecommendation().getAuthor());
            userSkillGuaranteeRepository.save(guarantee);
        }
        return skillMapper.toDto(skill);

    }

    private void validateOffers(List<SkillOffer> offers) {
        if (offers.size() < MINIMUM_OFFERS) {
            throw new DataValidationException("Скилл предложен менее 3 раз");
        }
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        getValidUser(userId);
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        validateOfferedSkills(skills);
        List<SkillDto> skillsDto = skills.stream().map(skillMapper::toDto).toList();
        return skillCandidateMapper.toDtoList(skillsDto);
    }

    private void validateOfferedSkills(List<Skill> skills) {
        if (skills.isEmpty()) {
            throw new DataValidationException("Пользователю не предложены скиллы");
        }
    }

}
