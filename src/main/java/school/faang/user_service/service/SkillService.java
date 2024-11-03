package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException();
        }
            Skill skillEntity = skillMapper.toEntity(skillDto);
            skillEntity = skillRepository.save(skillEntity);
            return skillMapper.toDto(skillEntity);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> allSkills = skillRepository.findAllByUserId(userId);
        return allSkills.stream().map(skillMapper::toDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> allOfferedSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> map2 = allOfferedSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        List<SkillCandidateDto> listFinal = skillMapper.toCandidateDto(map2);
        return listFinal;
    }
}

