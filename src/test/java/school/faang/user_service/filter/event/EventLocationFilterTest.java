package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.filter.EventLocationFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventLocationFilterTest {

    private EventLocationFilter eventLocationFilter;

    @BeforeEach
    public void setUp() {
        eventLocationFilter = new EventLocationFilter();
    }

    @Test
    public void testIsApplicable_NullLocation() {
        assertFalse(eventLocationFilter.isApplicable(new EventFilterDto()));
    }

    @Test
    public void testIsApplicable_WithLocation() {
        assertTrue(eventLocationFilter.isApplicable(EventFilterDto.builder().location("Location").build()));
    }

    @Test
    public void testApply() {
        Event event1 = Event.builder().location("location1").build();
        Event event2 = Event.builder().location("location2").build();
        Event event3 = Event.builder().location("location1").build();

        Stream<Event> streams = Stream.of(event1, event2, event3);
        Stream<Event> expectedStreams = Stream.of(event1, event3);

        EventFilterDto eventFilterDto = EventFilterDto.builder().location("location1").build();

        Stream<Event> resStream = eventLocationFilter.apply(streams, eventFilterDto);
        assertEquals(expectedStreams.toList(), resStream.toList());
    }
}