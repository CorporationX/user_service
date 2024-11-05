package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SkillAlreadyAcquiredException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Value("${application.skill.minOffersRequired}")
    private int minOffersRequired;

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        validateSkillAndUserId(skillId,userId);

        log.info("Attempting to acquire skill with ID {} for user ID {} ", skillId, userId);
        skillRepository.findUserSkill(skillId, userId)
                .ifPresent(skill -> {
                    log.warn("User with ID {} already has skill ID {}", userId, skillId);
                    throw new SkillAlreadyAcquiredException("User already has this skill. ");
                });

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        log.info("Found {} offers for skill ID {} for user ID {} ", skillOffers.size(), skillId, userId);

        if (skillOffers.size() < minOffersRequired) {
            log.warn("Not enough offers to acquire skill. Required: {}, Found: {}", minOffersRequired, skillOffers.size());
            throw new DataValidationException("Not enough offers to acquire the skill.");
        }

        skillRepository.assignSkillToUser(skillId, userId);
        log.info("Assign skill ID {} to user ID {}", skillId, userId);
        skillOffers.forEach(skillOffer -> {
            UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                    .user(skillOffer.getRecommendation().getReceiver())
                    .skill(skillOffer.getSkill())
                    .guarantor(skillOffer.getRecommendation().getAuthor())
                    .build();
            userSkillGuaranteeRepository.save(guarantee);
            log.info("Saved skill guarantee for skill ID {} from guarantor ID {} for user ID {}", skillId, skillOffer.getRecommendation().getAuthor().getId(), userId);
        });

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    log.error("Skill with ID {} not found after assignment", skillId);
                    return new DataValidationException("Skill not found.");
                });
        return skillMapper.toDto(skill);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        log.info("Retrieving offered skill for user ID {}",userId);
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> skillCountMap = offeredSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        return skillCountMap.entrySet().stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<SkillDto> getUserSkills(long userId) {
        log.info("Retrieving skills for user ID {}",userId);
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    public SkillDto create(SkillDto skillDto) {
        log.info("Creating skill with title '{}'",skillDto.getTitle());
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            log.warn("Skill with title '{}' already exists",skillDto.getTitle());
            throw new DataValidationException("Skill with this title already exists");
        }

        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getAllSkills() {
        log.info("Retrieving all skills");
        return skillRepository.findAll().stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }
    private void validateSkillAndUserId(Long skillId,Long userId){
        if (skillId == null || userId == null) {
            log.warn("Skill ID or User ID is null. Validation failed.");
            throw new DataValidationException("Skill ID and User ID cannot be null.");
        }
    }
}
