package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;

    public SkillDto create (SkillDto skill) {
        boolean skillExistsInDB = skillRepository.existsByTitle(skill.getTitle());

        if (skillExistsInDB) {
            throw new DataValidationException("Skill with name " + skill.getTitle() + " already exists in database.");
        }

        Skill skillEntity = skillRepository.save(skillMapper.toEntity(skill));

        return skillMapper.toDto(skillEntity);
    }
}
