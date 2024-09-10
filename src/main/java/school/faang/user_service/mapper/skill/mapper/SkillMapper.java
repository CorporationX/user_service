package school.faang.user_service.mapper.skill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    Skill toSkill(SkillDto skillDto);

    SkillDto toSkillDto(Skill skill);

    /*@Named("getSkills")
    default List<Skill> getSkills(List<SkillDto> skillDtos) {
        return skillDtos.stream()
                .map(this::toSkill)
                .collect(Collectors.toList());
    }

    @Named("getSkillDto")
    default List<SkillDto> getSkillsDtos(List<Skill> skills) {
        return skills.stream()
                .map(this::toSkillDto)
                .collect(Collectors.toList());
    }*/
}
