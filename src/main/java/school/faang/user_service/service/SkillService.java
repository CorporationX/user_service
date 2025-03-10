package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
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
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;

    private static final int MIN_SKILL_OFFERS = 3;
    private static final String ERROR_SKILL_EXIST = "That skill is already there.";
    private static final String ERROR_USER_HAS_SKILL = "User already has this skill.";
    private static final String ERROR_SKILL_NOT_FOUND = "Skill not found.";
    public static final String ERROR_NOT_ENOUGH_OFFERS = "Not enough offers to acquire this skill.";


    public SkillDto create(SkillDto skill) {
        log.info("Creating skill {} ...", skill.getTitle());
        if (skillRepository.existsByTitle(skill.getTitle())) {
            log.error(ERROR_SKILL_EXIST);
            throw new DataValidationException(ERROR_SKILL_EXIST);
        }
        Skill entity = skillMapper.toEntity(skill);
        entity = skillRepository.save(entity);
        return skillMapper.toDto(entity);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
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

        List<?> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            log.error(ERROR_NOT_ENOUGH_OFFERS);
            throw new DataValidationException(ERROR_NOT_ENOUGH_OFFERS);
        }
        skillRepository.assignSkillToUser(skillId, userId);
        return skillMapper.toDto(skillRepository.findById(skillId).orElseThrow(() ->
                new DataValidationException(ERROR_SKILL_NOT_FOUND)));
    }
}
