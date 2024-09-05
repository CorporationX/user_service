package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

@Component
public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getLocationPattern() != null &&
                !filters.getLocationPattern().isBlank();
    }

    @Override
    public Stream<Event> apply(List<Event> eventList, EventFilterDto eventFilterDto) {
        return eventList.stream()
                .filter(event -> event.getLocation()
                        .contains(eventFilterDto.getLocationPattern())
                );
    }
}
