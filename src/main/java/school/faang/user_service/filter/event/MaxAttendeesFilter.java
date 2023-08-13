package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class MaxAttendeesFilter implements Filter<Event, EventFilterDto> {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getMaxAttendees() != null;
    }

    @Override
    public Stream<Event> applyFilter(Stream<Event> events, EventFilterDto filterDto) {
        return events.filter(
                event -> event.getMaxAttendees() >= filterDto.getMaxAttendees()
        );
    }
}
