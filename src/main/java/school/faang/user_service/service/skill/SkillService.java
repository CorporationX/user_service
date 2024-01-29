package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;

    public SkillDto create (SkillDto skill) throws DataValidationException {
        checkIfSkillExists(skill.getTitle());

        Skill skillEntity = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    private void checkIfSkillExists (String skillTitle) throws DataValidationException {
        if (skillRepository.existsByTitle(skillTitle)) {
            throw new DataValidationException("Skill with name " + skillTitle + " already exists in database.");
        }
    }
}
