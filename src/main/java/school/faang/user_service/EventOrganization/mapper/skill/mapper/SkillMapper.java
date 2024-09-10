package school.faang.user_service.EventOrganization.mapper.skill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.EventOrganization.dto.event.EventDto;
import school.faang.user_service.EventOrganization.dto.event.EventFilterDto;
import school.faang.user_service.EventOrganization.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    Skill toSkill(SkillDto skillDto);

    SkillDto toSkillDto(Skill skill);

    @Named("getSkills")
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
    }
}
