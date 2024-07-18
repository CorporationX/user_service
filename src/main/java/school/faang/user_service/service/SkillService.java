package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private static final int MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle()))
            throw new DataValidationException("Skill with title '" + skillDto.getTitle() + "' already exists");

        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toDto(savedSkill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    SkillDto skillDto = skillMapper.toDto(entry.getKey());
                    long offersAmount = entry.getValue();
                    return new SkillCandidateDto(skillDto, offersAmount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId)
                .map(skill -> {
                    assignSkillGuarantees(skill, getUserById(userId), skillOfferRepository.findAllOffersOfSkill(skillId, userId));
                    return skillMapper.toDto(skill);
                })
                .orElseGet(() -> {
                    List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
                    if (offers.size() <= MIN_SKILL_OFFERS) {
                        throw new EntityNotFoundException("Not enough skill offers for skillId: " + skillId + " and userId: " + userId + ". Minimum required: " + MIN_SKILL_OFFERS);
                    }
                    skillRepository.assignSkillToUser(skillId, userId);
                    Skill assignedSkill = getAssignedSkill(skillId, userId);
                    assignSkillGuarantees(assignedSkill, getUserById(userId), offers);
                    return skillMapper.toDto(assignedSkill);
                });
    }

    private Skill getAssignedSkill(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId).orElseThrow(() -> new EntityNotFoundException("User skill not found"));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void assignSkillGuarantees(Skill skill, User user, List<SkillOffer> offers) {
        for (SkillOffer offer : offers) {
            User guarantor = offer.getRecommendation().getAuthor();
            if (!skillRepository.findUserSkill(guarantor.getId(), skill.getId()).isPresent()) {
                UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                        .skill(skill)
                        .user(user)
                        .guarantor(guarantor)
                        .build();
                userSkillGuaranteeRepository.save(guarantee);
            }
        }
    }
}