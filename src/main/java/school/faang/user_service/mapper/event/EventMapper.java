package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    EventDto toDto(Event event);

    @Mapping(target = "owner.id", ignore = true)
    Event toEntity(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> eventList);
}
