package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Transactional
    public SkillDto create(SkillDto skill) {
        validateSkill(skill);

        Skill skillToSave = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(skillToSave));
    }

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle().isBlank()) {
            throw new DataValidationException("Enter skill title, please");
        }

        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill with title " + skill.getTitle() + " already exists");
        }
    }
}
