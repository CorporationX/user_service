package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillMapper.toDto(skillRepository.save(skillMapper.toEntity(skill)));
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
