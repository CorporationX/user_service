package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> events);

    List<Event> toEntityList(List<EventDto> eventDtos);
}
