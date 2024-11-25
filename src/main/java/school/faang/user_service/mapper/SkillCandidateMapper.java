package school.faang.user_service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillCandidateMapper {
    private final SkillMapper skillMapper;

    public List<SkillCandidateDto> toCandidateDto(List<Skill> skills){
        List<SkillDto> skillDtoList = skillMapper.toDto(skills);
        List<SkillCandidateDto> skillCandidateDtoList = new ArrayList<>();

        Map<SkillDto, Long> map = skillDtoList.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        for (Map.Entry entry : map.entrySet()) {
            skillCandidateDtoList
                    .add(new SkillCandidateDto((SkillDto) entry.getKey(), (Long) entry.getValue()));
        }
        return skillCandidateDtoList;
    }
}
