package school.faang.user_service.service.event.filter;

import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public class PostEventFilter {
    public List<Event> postEventFilter(List<Event> events) {
        return events.stream()
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .toList();
    }
}