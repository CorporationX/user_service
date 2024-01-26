package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import java.util.stream.Stream;

@Component
public class EventDataFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getFromDateTime() != null && eventFilterDto.getToDateTime() != null;
    }

    @Override
    public Stream<EventDto> apply(Stream<EventDto> eventStream, EventFilterDto eventFilterDto) {
        return eventStream.filter(eventDto ->
                eventDto.getStartDate() != null
                        && eventDto.getEndDate() != null
                        && eventFilterDto.getFromDateTime().isAfter(eventDto.getStartDate())
                        && eventFilterDto.getToDateTime().isBefore(eventDto.getEndDate()));
    }
}
