package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static school.faang.user_service.service.event.filter.TestData.ALL_EVENTS;

class EventTitleFilterTest {
    private final EventTitleFilter eventTitleFilter = new EventTitleFilter();
    private EventFilterDto filter;
    private Stream<Event> eventsToFilter;
    private Stream<Event> expectedFilteredEvents;

    @BeforeEach
    void setUp() {
        eventsToFilter = ALL_EVENTS.stream();

        filter = new EventFilterDto();
        filter.setTitlePattern("forum");

        expectedFilteredEvents = Stream.of(ALL_EVENTS.get(0));
    }

    @Test
    void isApplicablePositiveTest() {
        var isApplicable = eventTitleFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForNullPatternTest() {
        filter.setTitlePattern(null);

        var isApplicable = eventTitleFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicableForBlankPatternTest() {
        filter.setTitlePattern("   ");

        var isApplicable = eventTitleFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveTest() {
        var actualFilteredUsers = eventTitleFilter.apply(eventsToFilter, filter);

        assertEquals(expectedFilteredEvents.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setTitlePattern("QA session");

        var actualFilteredUsers = eventTitleFilter.apply(eventsToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}