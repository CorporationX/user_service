package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    default List<SkillCandidateDto> toSkillCandidateDtoList(List<SkillDto> skills) {
        Map<SkillDto, SkillCandidateDto> skillsMap = new HashMap<>();
        for (SkillDto skillDto : skills) {
            if (skillsMap.containsKey(skillDto)) {
                skillsMap.get(skillDto).addOffersAmount();
            } else {
                skillsMap.put(skillDto, new SkillCandidateDto(skillDto, 1));
            }
        }
        return skillsMap.entrySet().stream().map(Map.Entry::getValue).toList();
    }

}
