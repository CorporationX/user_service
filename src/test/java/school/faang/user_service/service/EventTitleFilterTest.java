package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventTitleFilter;

import java.util.List;
import java.util.stream.Stream;

public class EventTitleFilterTest {
    private EventFilterDto eventFilterDto = EventFilterDto.builder().titlePattern("a").build();
    private EventTitleFilter eventTitleFilter = new EventTitleFilter();
    private Stream<Event> events = Stream.of(
            Event.builder().title("abcd").build(),
            Event.builder().title("bdc").build(),
            Event.builder().title("abcd").build(),
            Event.builder().title("bcd").build(),
            Event.builder().title("bcd").build()
    );

    @Test
    public void testApply() {
        List<Event> actualEvents = eventTitleFilter.apply(events, eventFilterDto).toList();
        Assertions.assertEquals(2, actualEvents.size());
        actualEvents.stream()
                .forEach(event -> Assertions.assertTrue(event.getTitle().contains(eventFilterDto.getTitlePattern())));
    }
}
