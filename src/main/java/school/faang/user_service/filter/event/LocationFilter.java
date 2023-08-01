package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;


@Component
public class LocationFilter implements Filter<Event, EventFilterDto> {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getLocation() != null && !filter.getLocation().isBlank();
    }

    @Override
    public Stream<Event> applyFilter(Stream<Event> events, EventFilterDto filterDto) {
        return events.filter(
                event -> event.getLocation().equalsIgnoreCase(filterDto.getLocation())
        );
    }
}