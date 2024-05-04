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

class EventSkillFilterTest {
    private final EventSkillFilter eventSkillFilter = new EventSkillFilter();
    private EventFilterDto filter;
    private Stream<Event> eventsToFilter;
    private Stream<Event> expectedFilteredEvents;

    @BeforeEach
    void setUp() {
        eventsToFilter = ALL_EVENTS.stream();

        filter = new EventFilterDto();
        filter.setSkillPattern("SQL");

        expectedFilteredEvents = ALL_EVENTS.stream();
    }

    @Test
    void isApplicablePositiveTest() {
        var isApplicable = eventSkillFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForNullPatternTest() {
        filter.setSkillPattern(null);

        var isApplicable = eventSkillFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicableForBlankPatternTest() {
        filter.setSkillPattern("   ");

        var isApplicable = eventSkillFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveTest() {
        var actualFilteredUsers = eventSkillFilter.apply(eventsToFilter, filter);

        assertEquals(expectedFilteredEvents.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setSkillPattern("C++");

        var actualFilteredUsers = eventSkillFilter.apply(eventsToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}