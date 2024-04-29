package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;


@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skillDto) {
        validateTitleRepetition(skillDto);

        Skill skill = skillMapper.DtoToSkill(skillDto);
        Skill skillSaved = skillRepository.save(skill);
        return skillMapper.skillToDto(skillSaved);
    }

    private void validateTitleRepetition(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Навык с таким именем уже существует в базе данных");
        }
    }
}
