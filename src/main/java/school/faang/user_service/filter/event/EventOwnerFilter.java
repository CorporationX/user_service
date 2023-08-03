package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventOwnerFilter implements EventFilter {
        @Override
        public boolean isApplicable(EventFilterDto eventFilterDto) {
//        return eventFilterDto.getOwnerIdPattern() != null;
            return Object.class.no);
        }

        @Override
        public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
            return events.filter(event ->
                    event.getOwner().getId().con(eventFilterDto.getOwnerIdPattern()));
        }
}