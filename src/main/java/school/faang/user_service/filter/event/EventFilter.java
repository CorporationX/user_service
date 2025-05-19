package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public interface EventFilter {

    boolean isApplicable(EventFilterDto filter);

    Stream<Event> apply(Stream<Event> events, EventFilterDto filter);
}
