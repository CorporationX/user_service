package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private static final String ERROR_SKILL_EXIST = "That skill is already there.";
    private static final String ERROR_USER_HAS_SKILL = "User already has this skill.";
    private static final String ERROR_SKILL_NOT_FOUND = "Skill not found.";
    private static final String ERROR_NOT_ENOUGH_OFFERS = "Not enough offers to acquire this skill.";
    private static final String ERROR_SKILL_EMPTY = "Skill title is empty";

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;

    public SkillDto create(SkillDto skill) {
        log.info("Creating skill {} ...", skill.getTitle());
        validateSkill(skill);
        if (skillRepository.existsByTitle(skill.getTitle())) {
            log.error(ERROR_SKILL_EXIST);
            throw new DataValidationException(ERROR_SKILL_EXIST);
        }
        return skillMapper.toDto(skillRepository.save(skillMapper.toEntity(skill)));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillMapper.toDtoList(skillRepository.findAllByUserId(userId));
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
                    skillCandidateDto.setSkill(skillMapper.toDto(entry.getKey()));
                    skillCandidateDto.setOffersAmount(entry.getValue());
                    return skillCandidateDto;
                })
                .collect(Collectors.toList());
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        log.info("Acquiring skill {} for the user {}", skillId, userId);
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            log.error(ERROR_USER_HAS_SKILL);
            throw new DataValidationException(ERROR_USER_HAS_SKILL);
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            log.error(ERROR_NOT_ENOUGH_OFFERS);
            throw new DataValidationException(ERROR_NOT_ENOUGH_OFFERS);
        }

        skillRepository.assignSkillToUser(skillId, userId);
        return skillRepository.findById(skillId)
                .map(skillMapper::toDto)
                .orElseThrow(() -> {
                    log.error(ERROR_SKILL_NOT_FOUND);
                    return new DataValidationException(ERROR_SKILL_NOT_FOUND);
                });
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isEmpty()) {
            log.error(ERROR_SKILL_EMPTY);
            throw new DataValidationException(ERROR_SKILL_EMPTY);
        }
    }
}
