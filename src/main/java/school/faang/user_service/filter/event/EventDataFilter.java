package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventDataFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getFromDateTime() != null && eventFilterDto.getToDateTime() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto eventFilterDto) {
        return eventStream.filter(event ->
                event.getStartDate() != null
                        && event.getEndDate() != null
                        && eventFilterDto.getFromDateTime().isAfter(event.getStartDate())
                        && eventFilterDto.getToDateTime().isBefore(event.getEndDate()));
    }
}
