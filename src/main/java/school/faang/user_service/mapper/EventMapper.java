package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.EventDto;
import school.faang.user_service.model.entity.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventDto toDto(Event event);
    Event toEntity(EventDto eventDto);
    List<EventDto> toListEventDto(List<Event> events);
}
