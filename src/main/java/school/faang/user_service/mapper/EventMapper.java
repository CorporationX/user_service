package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEntity(EventDto eventDto);
    EventDto toDto(Event event);
}
