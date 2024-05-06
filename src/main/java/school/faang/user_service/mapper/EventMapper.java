package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillMapper.class)
public interface EventMapper {
    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    List<EventDto> toEventsDto(List<Event> events);

    List<Event> toEvents(List<EventDto> eventsDto);
}
