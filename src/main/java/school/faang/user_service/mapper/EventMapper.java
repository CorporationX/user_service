package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.user.EventSearchResponse;
import school.faang.user_service.message.event.reindex.user.EventNested;
import school.faang.user_service.model.event.Event;
import school.faang.user_service.model.event.EventStatus;
import school.faang.user_service.model.event.EventType;

import java.util.List;

@Mapper(componentModel = "spring", uses = SkillMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(target = "owner", ignore = true)
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner.username", target = "usernameOwner")
    EventSearchResponse toSearchResponse(Event event);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    @Mapping(source = "owner.username", target = "usernameOwner")
    EventNested toEventNested(Event event);

    List<Event> toEntityList(List<EventDto> eventDtos);

    List<EventDto> toDtoList(List<Event> events);

    default String mapEventType(EventType eventType) {
        return eventType != null ? eventType.name() : null;
    }

    default String mapEventStatus(EventStatus eventStatus) {
        return eventStatus != null ? eventStatus.name() : null;
    }
}
