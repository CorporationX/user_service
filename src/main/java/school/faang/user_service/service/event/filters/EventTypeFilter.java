package school.faang.user_service.service.event.filters;


import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

@Component
public class EventTypeFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getTypePattern() != null &&
                eventFilterDto.getTypePattern().isBlank();
    }

    @Override
    public Stream<Event> apply(List<Event> eventList, EventFilterDto eventFilterDto) {
        return eventList.stream()
                .filter(event -> event.getType()
                        .getMessage()
                        .contains(eventFilterDto.getTypePattern())
                );
    }
}
