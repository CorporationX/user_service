package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;

    public SkillDto create (SkillDto skill) throws DataValidationException {
        checkIfSkillExists(skill.getTitle());

        Skill skillEntity = skillMapper.toEntity(skill);
        skillRepository.save(skillEntity);
        return skillMapper.toDto(skillEntity);
    }

    public List<SkillDto> getUserSkills (long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skillMapper.listToDto(skills);
    }

    private void checkIfSkillExists (String skillTitle) throws DataValidationException {
        if (skillRepository.existsByTitle(skillTitle)) {
            throw new DataValidationException("Skill with name " + skillTitle + " already exists in database.");
        }
    }

    public List<SkillCandidateDto> getOfferedSkills (long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        Map<Skill, Long> skillsMap = skills
                .stream()
                .collect(Collectors.groupingBy(skillDto -> skillDto, Collectors.counting()));

        List<SkillCandidateDto> skillCandidateDtos = skillsMap
                .entrySet().stream()
                .map(item -> new SkillCandidateDto(skillMapper.toDto(item.getKey()), item.getValue()))
                .toList();

        return skillCandidateDtos;
    }
}
