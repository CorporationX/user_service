package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
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

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Такой скилл уже существует!!!");
        }
        Skill skill = skillRepository.save(skillMapper.toEntity(skillDto));

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }
}
