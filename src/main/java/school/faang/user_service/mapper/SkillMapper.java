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

    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skilldto);

    List<SkillDto> toDtoList(List<Skill> skills);
  
    List<Skill> toEntityList(List<SkillDto> skillsdto);
  
    default List<SkillCandidateDto> toSkillCandidateDtoList(List<Skill> skillList) {
        Map<String, List<Skill>> skillsByTitle = skillList.stream()
                .collect(Collectors.groupingBy(Skill::getTitle));

        return skillsByTitle.values()
                .stream()
                .map(skills -> {
                    SkillDto skillDto = toDto(skills.get(0));
                    long offersAmount = skills.size();
                    return new SkillCandidateDto(skillDto, offersAmount);
                })
                .toList();
    }
}
