package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventOwnerIdFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getOwnerId() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto eventFilterDto) {
        return eventStream.filter(event -> event.getOwner().getId() == eventFilterDto.getOwnerId());
    }
}
