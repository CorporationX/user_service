package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventStatusFilterTest {

    private EventStatusFilter eventStatusFilter;
    private EventFilterDto filter;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        eventStatusFilter = new EventStatusFilter();
        filter = EventFilterDto.builder().build();
        firstEvent = new Event();
        secondEvent = new Event();
    }

    @Test
    void testIsApplicableWithNullStatus() {
        filter.setEventStatusPattern(null);

        assertFalse(eventStatusFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithStatus() {
        filter.setEventStatusPattern(EventStatus.CANCELED);

        assertTrue(eventStatusFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithNonMatchingStatus() {
        filter.setEventStatusPattern(EventStatus.IN_PROGRESS);
        firstEvent.setStatus(EventStatus.CANCELED);
        secondEvent.setStatus(EventStatus.PLANNED);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = eventStatusFilter.apply(events, filter).toList();

        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testApplyWithMatchingStatus() {
        filter.setEventStatusPattern(EventStatus.IN_PROGRESS);
        firstEvent.setStatus(EventStatus.IN_PROGRESS);
        secondEvent.setStatus(EventStatus.PLANNED);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = eventStatusFilter.apply(events, filter).toList();

        assertEquals(1, filteredEvents.size());
    }
}