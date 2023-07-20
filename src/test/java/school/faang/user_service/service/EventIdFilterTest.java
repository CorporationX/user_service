package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventIdFilter;

import java.util.List;
import java.util.stream.Stream;

public class EventIdFilterTest {
    private EventFilterDto eventFilterDto = EventFilterDto.builder().eventId(1L).build();
    private EventIdFilter eventIdFilter = new EventIdFilter();
    private List<Event> events = List.of(
            Event.builder().id(1L).build(),
            Event.builder().id(2L).build(),
            Event.builder().id(3L).build()
    );

    @Test
    public void testApply() {
        Stream<Event> eventStream = events.stream();
        List<Event> actualEvents = eventIdFilter.apply(eventStream, eventFilterDto).toList();
        Assertions.assertEquals(1, actualEvents.size());
        actualEvents.stream()
                .forEach(event -> Assertions.assertEquals(event.getId(), eventFilterDto.getEventId()));
    }

}
