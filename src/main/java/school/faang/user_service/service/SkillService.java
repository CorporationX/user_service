package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillMapper.toDto(skillRepository.save(skillMapper.toEntity(skill)));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().map(skillMapper::toDto).toList();
    }
    private void validateSkill(SkillDto skill) {
        if (skill.getTitle().isBlank() || skill.getTitle() == null) {
            throw new DataValidationException("title doesn't exist");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(skill.getTitle() + " already exist");
        }
    }
}
