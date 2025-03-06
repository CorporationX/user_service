package school.faang.user_service.service.event_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public interface Filter {
    boolean isApplicable(EventFilterDto filter);

    Stream<Event> apply(Stream<Event> event, EventFilterDto filter);
}