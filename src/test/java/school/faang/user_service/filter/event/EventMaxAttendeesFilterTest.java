package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.filter.EventMaxAttendeesFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventMaxAttendeesFilterTest {

    private EventMaxAttendeesFilter eventMaxAttendeesFilter;

    @BeforeEach
    public void setUp() {
        eventMaxAttendeesFilter = new EventMaxAttendeesFilter();
    }

    @Test
    public void testIsApplicable_NullMaxAttendees() {
        assertFalse(eventMaxAttendeesFilter.isApplicable(new EventFilterDto()));
    }

    @Test
    public void testIsApplicable_WithMaxAttendees() {
        assertTrue(eventMaxAttendeesFilter.isApplicable(EventFilterDto.builder().maxAttendees(10).build()));
    }

    @Test
    public void testApply() {
        Event event1 = Event.builder().maxAttendees(15).build();
        Event event2 = Event.builder().maxAttendees(20).build();
        Event event3 = Event.builder().maxAttendees(15).build();

        Stream<Event> streams = Stream.of(event1, event2, event3);
        Stream<Event> expectedStreams = Stream.of(event1, event3);

        EventFilterDto eventFilterDto = EventFilterDto.builder().maxAttendees(15).build();

        Stream<Event> resStream = eventMaxAttendeesFilter.apply(streams, eventFilterDto);
        assertEquals(expectedStreams.toList(), resStream.toList());
    }
}