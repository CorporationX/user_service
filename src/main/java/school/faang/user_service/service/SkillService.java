package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
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

    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            System.out.println("kek");
            throw new DataValidationException("Такой скилл уже существует!!!");
        }
        Skill skill = skillRepository.save(skillMapper.toEntity(skillDto));

        return skillMapper.toDto(skill);
    }
}
