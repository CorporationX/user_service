package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.redis.events.EventInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "mapListToId")
    EventDto toDto(Event event);

    @Mapping(target = "relatedSkills", ignore = true)
    Event toEntity(EventDto eventDto);

    @Mapping(source = "attendees", target = "attendeesIds", qualifiedByName = "mapAttendees")
    EventInfo toEventStartEvent(Event event);

    EventDto update(@MappingTarget EventDto target, EventDto updatingSource);

    @Named("mapListToId")
    default List<Long> mapListToId(List<Skill> objects) {
        return objects.stream()
            .map(Skill::getId)
            .collect(Collectors.toList());
    }

    @Named("mapAttendees")
    default List<Long> mapAttendees(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }
}