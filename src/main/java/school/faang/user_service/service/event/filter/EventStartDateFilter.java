package school.faang.user_service.service.event.filter;

import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

 class EventStartDateFilter implements EventFilter {
     @Override
     public boolean isApplicable(EventFilterDto filters) {
         return filters.getStartDatePattern() != null;
     }

     @Override
     public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
         return events
                 .filter(event -> event.getStartDate().isAfter(filters.getStartDatePattern())
                         || event.getStartDate().equals(filters.getStartDatePattern()));
    }
}
