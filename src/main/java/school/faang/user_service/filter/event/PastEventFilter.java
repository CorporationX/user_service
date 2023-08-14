package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class PastEventFilter implements Filter<Event, EventFilterDto> {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getIsNeedPastEvents();
    }

    @Override
    public Stream<Event> applyFilter(Stream<Event> events, EventFilterDto filter) {
        return events
                .filter(event -> event.getEndDate().isAfter(LocalDateTime.now()));
    }
}
