package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.service.event.filter.TestData.ALL_EVENTS;

class EventStatusFilterTest {
    private final EventStatusFilter eventStatusFilter = new EventStatusFilter();
    private EventFilterDto filter;
    private Stream<Event> eventsToFilter;
    private Stream<Event> expectedFilteredEvents;

    @BeforeEach
    void setUp() {
        eventsToFilter = ALL_EVENTS.stream();

        filter = new EventFilterDto();
        filter.setStatusPattern("Progress");

        expectedFilteredEvents = Stream.of(ALL_EVENTS.get(1));
    }

    @Test
    void isApplicablePositiveTest() {
        var isApplicable = eventStatusFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForNullPatternTest() {
        filter.setStatusPattern(null);

        var isApplicable = eventStatusFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicableForBlankPatternTest() {
        filter.setStatusPattern("   ");

        var isApplicable = eventStatusFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveTest() {
        var actualFilteredUsers = eventStatusFilter.apply(eventsToFilter, filter);

        assertEquals(expectedFilteredEvents.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setStatusPattern("Completed");

        var actualFilteredUsers = eventStatusFilter.apply(eventsToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}