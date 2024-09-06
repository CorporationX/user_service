package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;


import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapIdsSkills")
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);

    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Event toEvent(EventDto eventDto);

    @Mapping(source = "relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapIdsSkills")
    @Mapping(source = "owner.id", target = "ownerId")
    List<EventDto> toDto(List<Event> events);

    @Mapping(source = "relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapIdsSkills")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.username", target = "ownerUsername")
    List<EventDto> toFilteredEventsDto(List<Event> events);

    @Mapping(source = "event.relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapIdsSkills")
    @Mapping(source = "event.owner.id", target = "ownerId")
    EventWithSubscribersDto toEventWithSubscribersDto(Event event, Integer subscribersCount);

    @Named("mapIdsSkills")
    default List<Long> mapIdsSkills(List<Skill> skills) {
        return skills == null ? Collections.emptyList() : skills.stream()
                .map(Skill::getId)
                .toList();
    }
    void updateEventFromDto(@MappingTarget Event existingEvent, Event event);
}
