package school.faang.user_service.filter.event;

import school.faang.user_service.entity.event.Event;


public interface EventFilter {

    boolean apply(Event event, EventFilterDto filter);
}
