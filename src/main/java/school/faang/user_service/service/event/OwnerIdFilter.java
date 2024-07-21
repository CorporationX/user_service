package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class OwnerIdFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getOwnerId() != 0;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events
                .filter(event -> eventFilterDto.getOwnerId() == event.getOwner().getId());
    }
}
