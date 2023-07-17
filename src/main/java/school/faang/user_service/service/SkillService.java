package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidate;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidate skillCandidate;
    private final UserService userService;

    public List<SkillDto> getUserSkills(long userId) {
        userService.checkUserById(userId);
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
