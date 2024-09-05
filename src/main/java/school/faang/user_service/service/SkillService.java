package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SkillAssignmentException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private final SkillMapper mapper;

    @Transactional
    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title '" + skillDto.getTitle() + "' already exists.");
        }
        final Skill skillEntity = skillRepository.save(mapper.toSkillEntity(skillDto));

        return mapper.toSkillDto(skillEntity);
    }

    public List<SkillDto> getUserSkills(long userId) {
        final List<Skill> skills = skillRepository.findAllByUserId(userId);

        return mapper.toListSkillDto(skills);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User with id '" + userId + "' does not exist.");
        }
        final List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);

        return offeredSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new SkillCandidateDto(mapper.toSkillDto((entry.getKey())), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillRepository.findUserSkill(skillId, userId)
                .ifPresent(skill -> {
                    throw new SkillAssignmentException("Skill already acquired by the user.");
                });

        final List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new SkillAssignmentException("Not enough skill offers to acquire this skill.");
        }

        skillRepository.assignSkillToUser(skillId, userId);
        setGuarantors(skillId, userId);

        Skill assignedSkill = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new RuntimeException("Skill not found after assignment."));
        return mapper.toSkillDto(assignedSkill);
    }

    private void setGuarantors(long skillId, long userId) {
        List<Long> guarantorIds = skillOfferRepository.findSkillOfferGuarantors(skillId, userId);
        guarantorIds.stream()
                .map(guarantorId -> {
                    UserSkillGuarantee guarantee = new UserSkillGuarantee();
                    guarantee.setUser(userRepository.findById(userId).orElseThrow(
                            () -> new RuntimeException("User not found.")));
                    guarantee.setSkill(skillRepository.findById(skillId).orElseThrow(
                            () -> new RuntimeException("Skill not found.")));
                    guarantee.setGuarantor(userRepository.findById(guarantorId).orElseThrow(
                            () -> new RuntimeException("Guarantor not found.")));
                    return guarantee;
                })
                .forEach(userSkillGuaranteeRepository::save);
    }
}
