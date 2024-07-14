package school.faang.user_service.filter.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;

import java.util.stream.Stream;

@Component
public class OwnerPatternFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getOwnerPattern() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filters) {
        // Фильтрую пользователя только по фамилии
        return eventStream.filter(event -> event.getOwner().getUsername()
                .contains(filters.getOwnerPattern().getUsername()));
    }

    @Override
    public boolean test(Event event, EventFilterDto filters) {
        return event.getOwner().getUsername()
                .contains(filters.getOwnerPattern().getUsername());
    }
}
