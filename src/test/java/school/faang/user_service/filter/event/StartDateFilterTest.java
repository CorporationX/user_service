package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StartDateFilterTest {

    private StartDateFilter startDateFilter;
    private EventFilterDto filter;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        startDateFilter = new StartDateFilter();
        filter = EventFilterDto.builder().build();
        firstEvent = new Event();
        secondEvent = new Event();
    }
    @Test
    void testIsApplicableWithNullStartDate() {
        filter.setStartDatePattern(null);

        assertFalse(startDateFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValidStartDate() {
        filter.setStartDatePattern(LocalDateTime.now().plusDays(1));

        assertTrue(startDateFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithInvalidStartDate() {
        filter.setStartDatePattern(LocalDateTime.now().plusDays(10));
        firstEvent.setStartDate(LocalDateTime.now());
        secondEvent.setStartDate(LocalDateTime.now().plusDays(1));
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = startDateFilter.apply(events, filter).toList();

        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testApplyWithValidStartDate() {
        filter.setStartDatePattern(LocalDateTime.now().minusDays(10));
        firstEvent.setStartDate(LocalDateTime.now());
        secondEvent.setStartDate(LocalDateTime.now().plusDays(1));
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = startDateFilter.apply(events, filter).toList();

        assertEquals(2, filteredEvents.size());
    }
}