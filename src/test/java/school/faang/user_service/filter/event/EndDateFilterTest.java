package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EndDateFilterTest {

    private EndDateFilter endDateFilter;
    private EventFilterDto filter;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        endDateFilter = new EndDateFilter();
        filter = EventFilterDto.builder().build();
        firstEvent = new Event();
        secondEvent = new Event();
    }

    @Test
    void testIsApplicableWithNullEndDate() {
        filter.setEndDatePattern(null);

        assertFalse(endDateFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValidEndDate() {
        filter.setEndDatePattern(LocalDateTime.now().plusDays(1));

        assertTrue(endDateFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithInvalidEndDate() {
        filter.setEndDatePattern(LocalDateTime.now());
        firstEvent.setEndDate(LocalDateTime.now().plusMinutes(10));
        secondEvent.setEndDate(LocalDateTime.now().plusDays(1));
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = endDateFilter.apply(events, filter).toList();

        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testApplyWithValidEndDate() {
        filter.setEndDatePattern(LocalDateTime.now().plusDays(10));
        firstEvent.setEndDate(LocalDateTime.now());
        secondEvent.setEndDate(LocalDateTime.now().plusDays(1));
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = endDateFilter.apply(events, filter).toList();

        assertEquals(2, filteredEvents.size());
    }
}