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

class EventTypeFilterTest {
    private final EventTypeFilter eventTypeFilter = new EventTypeFilter();
    private EventFilterDto filter;
    private Stream<Event> eventsToFilter;
    private Stream<Event> expectedFilteredEvents;

    @BeforeEach
    void setUp() {
        eventsToFilter = ALL_EVENTS.stream();

        filter = new EventFilterDto();
        filter.setTypePattern("Meeting");

        expectedFilteredEvents = Stream.of(ALL_EVENTS.get(0));
    }

    @Test
    void isApplicablePositiveTest() {
        var isApplicable = eventTypeFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForNullPatternTest() {
        filter.setTypePattern(null);

        var isApplicable = eventTypeFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicableForBlankPatternTest() {
        filter.setTypePattern("   ");

        var isApplicable = eventTypeFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveTest() {
        var actualFilteredUsers = eventTypeFilter.apply(eventsToFilter, filter);

        assertEquals(expectedFilteredEvents.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setTypePattern("QA session");

        var actualFilteredUsers = eventTypeFilter.apply(eventsToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}