package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.skill.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@Component
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    public SkillDto create(SkillDto skill) {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));
            return skillMapper.toDto(savedSkill);
        } else throw new DataValidationException("Навык с таким именем уже существует");
    }
}