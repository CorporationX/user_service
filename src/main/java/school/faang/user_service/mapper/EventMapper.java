package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    Event toEntity(EventDto eventdto);

    default List<EventDto> toDtoList(List<Event> events) {
        return events.stream()
                .map(event -> toDto(event))
                .collect(Collectors.toList());
    }

    default List<Event> toEntityList(List<EventDto> eventsDto) {
        return eventsDto.stream()
                .map(eventDto -> toEntity(eventDto))
                .collect(Collectors.toList());
    }
}