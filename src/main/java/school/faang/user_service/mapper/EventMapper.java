package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "relatedSkills", source = "relatedSkills")
    EventDto toDto(Event event);

    @Mapping(target = "relatedSkills", source = "relatedSkills")
    Event toEntity(EventDto eventDto);

    List<SkillDto> mapSkills(List<Skill> skills);

    List<Skill> mapSkillDtos(List<SkillDto> skillDtos);
}
