package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;
    public SkillDto create(SkillDto skill) {
        skillValidator.validateSkill(skill);
        Skill convertedSkill = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(convertedSkill));
    }
}
