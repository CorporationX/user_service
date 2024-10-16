package school.faang.user_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.dto.SkillCandidateDto;
import school.faang.user_service.model.dto.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.UserSkillGuarantee;
import school.faang.user_service.model.entity.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SkillAssignmentException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.model.event.SkillAcquiredEvent;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService {
    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private final SkillMapper mapper;
    private final SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    @Override
    @Transactional
    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title '" + skillDto.getTitle() + "' already exists.");
        }
        final Skill skillEntity = skillRepository.save(mapper.toSkillEntity(skillDto));

        return mapper.toSkillDto(skillEntity);
    }

    @Override
    public List<SkillDto> getUserSkills(long userId) {
        final List<Skill> skills = skillRepository.findAllByUserId(userId);

        return mapper.toListSkillDto(skills);
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User with id '" + userId + "' does not exist.");
        }
        final List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);

        Map<Skill, Long> skillAmountMap = offeredSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));

        return skillAmountMap.entrySet().stream()
                .map(entry -> new SkillCandidateDto(mapper.toSkillDto((entry.getKey())), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
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
        log.info("Assigning skill ID {} to user ID {}", skillId, userId);

        skillRepository.assignSkillToUser(skillId, userId);

        skillAcquiredEventPublisher.publish(new SkillAcquiredEvent(userId, skillId));
        log.info("Publishing SkillAcquiredEvent for user ID {} and skill ID {}", userId, skillId);

        setGuarantors(skillOffers);

        Skill assignedSkill = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new RuntimeException("Skill not found after assignment."));
        return mapper.toSkillDto(assignedSkill);
    }

    private void setGuarantors(List<SkillOffer> skillOffers) {
        userSkillGuaranteeRepository.saveAll(skillOffers.stream()
                .map(offeredSkill -> UserSkillGuarantee.builder()
                        .user(offeredSkill.getRecommendation().getReceiver())
                        .skill(offeredSkill.getSkill())
                        .guarantor(offeredSkill.getRecommendation().getAuthor())
                        .build())
                .distinct()
                .toList());
    }
}
