package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto toSkillDto(Skill skill);

    List<SkillDto> toSkillDtoList(List<Skill> skillList);

    Skill toSkill(SkillDto skill);

    default List<SkillCandidateDto> toSkillCandidateDtoList(List<Skill> skillList) {
        Map<String, List<Skill>> skillsByTitle = skillList.stream()
                .collect(Collectors.groupingBy(Skill::getTitle));

        return skillsByTitle.values()
                .stream()
                .map(skills -> {
                    SkillDto skillDto = toSkillDto(skills.get(0));
                    long offersAmount = skills.size();
                    return new SkillCandidateDto(skillDto, offersAmount);
                })
                .toList();
    }
}
