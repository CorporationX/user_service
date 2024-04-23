package school.faang.user_service.service.event.filter;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import java.util.List;

public interface EventFilter {

    boolean isApplicable(EventFilterDto filter);

    void apply(List<Event> events, EventFilterDto filter);

}
