package school.faang.user_service.service.event.filters;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class EventEndDateFilter implements EventFilter {
  @Override
  public boolean isApplicable(EventFilterDto filters) {
    return filters.getEndDate() != null;
  }

  @Override
  public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
    return events.filter(event -> event.getEndDate().isAfter(filters.getEndDate()));
  }
}
