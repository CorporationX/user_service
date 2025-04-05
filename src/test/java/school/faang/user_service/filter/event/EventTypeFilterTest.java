package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventTypeFilterTest {

    private EventTypeFilter eventTypeFilter;
    private EventFilterDto filter;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        eventTypeFilter = EventTypeFilter.TYPE;
        filter = EventFilterDto.builder().build();
        firstEvent = new Event();
        secondEvent = new Event();
    }

    @Test
    void testIsApplicableWithNullType() {
        filter.setEventTypePattern(null);

        assertFalse(eventTypeFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithType() {
        filter.setEventTypePattern(EventType.POLL);

        assertTrue(eventTypeFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithNonMatchingType() {
        filter.setEventTypePattern(EventType.GIVEAWAY);
        firstEvent.setType(EventType.MEETING);
        secondEvent.setType(EventType.WEBINAR);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = eventTypeFilter.apply(events, filter).toList();

        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testApplyWithMatchingType() {
        filter.setEventTypePattern(EventType.GIVEAWAY);
        firstEvent.setType(EventType.GIVEAWAY);
        secondEvent.setType(EventType.WEBINAR);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = eventTypeFilter.apply(events, filter).toList();

        assertEquals(1, filteredEvents.size());
    }
}