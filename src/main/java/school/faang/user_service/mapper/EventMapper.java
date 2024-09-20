package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {

    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> events);
}
