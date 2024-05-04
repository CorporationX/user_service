package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.service.event.filter.TestData.ALL_EVENTS;

class EventEndDateFilterTest {
    private final EventEndDateFilter eventEndDateFilter = new EventEndDateFilter();
    private EventFilterDto filter;
    private Stream<Event> eventsToFilter;
    private Stream<Event> expectedFilteredEvents;

    @BeforeEach
    void setUp() {
        eventsToFilter = ALL_EVENTS.stream();

        filter = new EventFilterDto();
        filter.setEndDatePattern(LocalDateTime.of(2024, 6, 1, 12, 0));

        expectedFilteredEvents = Stream.of(ALL_EVENTS.get(1));
    }

    @Test
    void isApplicablePositiveTest() {
        var isApplicable = eventEndDateFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForNullPatternTest() {
        filter.setEndDatePattern(null);

        var isApplicable = eventEndDateFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveTest() {
        var actualFilteredUsers = eventEndDateFilter.apply(eventsToFilter, filter);

        assertEquals(expectedFilteredEvents.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setEndDatePattern(LocalDateTime.of(2024, 3, 10, 12, 0));

        var actualFilteredUsers = eventEndDateFilter.apply(eventsToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}