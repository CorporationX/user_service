package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    Event eventDtoToEntity(EventDto eventDto);

    @Mapping(source = "relatedSkills", target = "relatedSkillsIds",
            qualifiedByName = "map")
    EventDto eventToDto(Event event);

    @Named("map")
    default List<Long> map(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    List<EventDto> listEventsToDto(List<Event> eventList);
}
