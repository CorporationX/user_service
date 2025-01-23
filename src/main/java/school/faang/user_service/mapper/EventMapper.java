package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.dto.event.EventDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    Event toEntity(EventDto dto);

    @Mapping(source = "type", target = "eventType")
    @Mapping(source = "status", target = "eventStatus")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillIds", qualifiedByName = "map")
    EventDto toDto(Event entity);

    List<EventDto> toDto(List<Event> events);

    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    void update(@MappingTarget Event event, EventDto eventDto);

    @Named("map")
    default List<Long> map(List<Skill> relatedSkills) {
        return relatedSkills.stream().map(Skill::getId).toList();
    }
}
