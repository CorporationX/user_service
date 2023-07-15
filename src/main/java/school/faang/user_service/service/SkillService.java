package school.faang.user_service.service;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidate;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Data
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidate skillCandidate;

    public List<SkillDto> getUserSkills(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new RuntimeException("user is not found");
        }
        List<Skill> skillsOfUsers = skillRepository.findAllByUserId(userId);
        return skillsOfUsers.stream().map(skillMapper::toDTO).toList();
    }

    public SkillDto create(SkillDto skillDto) {
        Skill skillFromDto = skillMapper.toEntity(skillDto);
        if (skillRepository.existsByTitle(skillFromDto.getTitle())) {
            throw new DataValidationException("The skill already exists");
        }
        return skillMapper.toDTO(skillRepository.save(skillFromDto));
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> offeredSkillsAndCount = offeredSkills.stream()
                .collect(Collectors.toMap(Function.identity(), value -> 1L, Long::sum));
        return offeredSkillsAndCount.entrySet().stream()
                .map(skillIntegerEntry ->
                        skillCandidate.mapToDTO(skillIntegerEntry.getKey(), skillIntegerEntry.getValue()))
                .toList();
    }


}
