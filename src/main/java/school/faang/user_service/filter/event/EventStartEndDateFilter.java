package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class EventStartEndDateFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getStartDate() != null || filter.getEndDate() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        LocalDateTime start = filter.getStartDate() != null ? filter.getStartDate() : LocalDateTime.now();
        LocalDateTime end = filter.getEndDate();

        return events.filter(event ->
                event.getStartDate().isAfter(start) &&
                        (end == null || event.getEndDate().isBefore(end))
        );
    }
}