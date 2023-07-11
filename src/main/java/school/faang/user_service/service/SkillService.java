package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    private final SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Такой скилл уже существует!!!");
        }
        Skill skill = skillRepository.save(skillMapper.toEntity(skillDto));
        return skillMapper.toDto(skill);
    }
}
