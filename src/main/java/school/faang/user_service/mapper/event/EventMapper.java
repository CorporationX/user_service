package school.faang.user_service.mapper.event;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import java.util.List;

@Component
@Mapper(componentModel = "spring" , injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    Event toEvent(EventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    List<EventDto> toListDto(List<Event> events);

    @Mapping(source = "ownerId", target = "owner.id")
    List<Event> toListEntity(List<EventDto> eventDtoList);
}