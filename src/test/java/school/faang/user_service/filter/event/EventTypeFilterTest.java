package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.service.event.filter.EventTypeFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventTypeFilterTest {

    private EventTypeFilter eventTypeFilter;

    @BeforeEach
    public void setUp() {
        eventTypeFilter = new EventTypeFilter();
    }

    @Test
    public void testIsApplicable_NullType() {
        assertFalse(eventTypeFilter.isApplicable(new EventFilterDto()));
    }

    @Test
    public void testIsApplicable_WithType() {
        assertTrue(eventTypeFilter.isApplicable(EventFilterDto.builder().type(EventType.POLL).build()));
    }

    @Test
    public void testApply() {
        Event event1 = Event.builder().type(EventType.WEBINAR).build();
        Event event2 = Event.builder().type(EventType.PRESENTATION).build();
        Event event3 = Event.builder().type(EventType.WEBINAR).build();

        Stream<Event> streams = Stream.of(event1, event2, event3);
        Stream<Event> expectedStreams = Stream.of(event1, event3);

        EventFilterDto eventFilterDto = EventFilterDto.builder().type(EventType.WEBINAR).build();

        Stream<Event> resStream = eventTypeFilter.apply(streams, eventFilterDto);
        assertEquals(expectedStreams.toList(), resStream.toList());
    }
}