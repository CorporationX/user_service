package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {

    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> events);


    default List<EventDto> map(List<Event> eventList) {
        List<EventDto> eventDtoList = new ArrayList<>();
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            eventDtoList.add(toDto(event));
        }
        return eventDtoList;
    }

}
