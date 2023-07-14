package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("This skill already exist");
        }
        Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));
        return skillMapper.toDTO(savedSkill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<SkillDto> list = new ArrayList<>();
        for (Skill skill : skillRepository.findAllByUserId(userId)) {
            list.add(skillMapper.toDTO(skill));
        }
        return list;
    }
}
