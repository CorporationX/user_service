package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill title already exists");
        }

        Skill entity = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(entity));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().
                map(skillMapper::toDto).
                collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().
                map(skillMapper::toSkillCandidateDto).collect(Collectors.toList());
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("Skill already exists");
        }

        List<Skill> offers = skillRepository.findAllByUserId(userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new IllegalArgumentException("Not enough offers to acquire this skill");
        }

        skillRepository.assignSkillToUser(userId, skillId);

        Skill skill = skillRepository.findById(skillId).
                orElseThrow(() -> new DataValidationException("Skill not found"));
        return skillMapper.toDto(skill);
    }
}
