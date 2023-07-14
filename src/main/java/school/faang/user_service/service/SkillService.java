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
    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);

        Skill skillToSave = skillMapper.toEntity(skillDto);
        return skillMapper.toDto(skillRepository.save(skillToSave));
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Enter skill title, please");
        }

        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title " + skillDto.getTitle() + " already exists");
        }
    }
}
