package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventUsernameFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getOwnerUsername() != null;
    }

    @Override
    public void apply(Stream<Event> eventStream, EventFilterDto eventFilterDto) {
        eventStream.filter(event -> event.getOwner().getUsername().equals(eventFilterDto.getOwnerUsername()));
    }
}
