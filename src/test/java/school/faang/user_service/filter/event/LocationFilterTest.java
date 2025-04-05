package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LocationFilterTest {

    private LocationFilter locationFilter;
    private EventFilterDto filter;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        locationFilter = new LocationFilter();
        filter = EventFilterDto.builder().build();
        firstEvent = new Event();
        secondEvent = new Event();
    }

    @Test
    void testIsApplicableWithNullLocation() {
        filter.setLocationPattern(null);

        assertFalse(locationFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithBlankLocation() {
        filter.setLocationPattern("    ");

        assertFalse(locationFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValidLocation() {
        filter.setLocationPattern("Minsk");

        assertTrue(locationFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithNonMatchingLocation() {
        filter.setLocationPattern("Grodno");
        firstEvent.setLocation("Minsk");
        secondEvent.setLocation("Mogilev");
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = locationFilter.apply(events, filter).toList();

        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testApplyWithMatchingLocation() {
        filter.setLocationPattern("Grodno");
        firstEvent.setLocation("grodno");
        secondEvent.setLocation("Mogilev");
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = locationFilter.apply(events, filter).toList();

        assertEquals(1, filteredEvents.size());
    }
}