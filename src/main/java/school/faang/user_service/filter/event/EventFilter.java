package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public interface EventFilter {  //TODO один общий интерфейс с дженерик типом для всех дто
    boolean isApplicable(EventFilterDto eventFilterDto);

    Stream<Event> apply(Stream<Event> eventStream, EventFilterDto eventFilterDto);
}
