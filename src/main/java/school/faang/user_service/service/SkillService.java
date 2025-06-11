package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;


    public SkillDto create(SkillDto skill) {
        //todo: check the title
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill  already exists");
        }
        Skill entity = skillMapper.toEntity(skill);
        skillRepository.save(entity);
        return skillMapper.toDto(entity);
    }

    public List<SkillDto> getUserSkills(long usedId) {
        return skillRepository.findAllByUserId(usedId).stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }
}
