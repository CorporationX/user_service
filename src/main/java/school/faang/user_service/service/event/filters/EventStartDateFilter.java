package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventStartDateFilter implements EventFilter {
  @Override
  public boolean isApplicable(EventFilterDto filters) {
    return filters.getStartDate() != null;
  }

  @Override
  public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
    return events.filter(event -> event.getStartDate().isAfter(filters.getStartDate()));
  }
}
