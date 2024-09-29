package school.faang.user_service.mapper.event;

import org.mapstruct.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkills", ignore = true)
    EventDto toDto(Event event);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    Event toEvent(EventDto eventDto);

    @AfterMapping
    default void mapRelatedSkillsTargetEventDto(Event event, @MappingTarget EventDto eventDto) {
        List<Long> skillsId = event.getRelatedSkills().stream()
                .map(Skill::getId)
                .toList();

        eventDto.setRelatedSkills(skillsId);
    }
}