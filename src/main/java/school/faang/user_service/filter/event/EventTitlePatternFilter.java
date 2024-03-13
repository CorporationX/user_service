package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventTitlePatternFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getTitlePattern() != null && !eventFilterDto.getTitlePattern().isBlank();
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto eventFilterDto) {
        return eventStream.filter(event -> event.getTitle().contains(eventFilterDto.getTitlePattern()));
    }
}
