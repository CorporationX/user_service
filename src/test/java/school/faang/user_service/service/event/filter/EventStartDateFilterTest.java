package school.faang.user_service.service.event.filter;

import net.bytebuddy.dynamic.ClassFileLocator;
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

class EventStartDateFilterTest {
    private final EventStartDateFilter eventStartDateFilter = new EventStartDateFilter();
    private EventFilterDto filter;
    private Stream<Event> eventsToFilter;
    private Stream<Event> expectedFilteredEvents;

    @BeforeEach
    void setUp() {
        eventsToFilter = ALL_EVENTS.stream();

        filter = new EventFilterDto();
        filter.setStartDatePattern(LocalDateTime.of(2024, 4, 10, 12, 0));

        expectedFilteredEvents = ALL_EVENTS.stream();
    }

    @Test
    void isApplicablePositiveTest() {
        var isApplicable = eventStartDateFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForNullPatternTest() {
        filter.setStartDatePattern(null);

        var isApplicable = eventStartDateFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveTest() {
        var actualFilteredUsers = eventStartDateFilter.apply(eventsToFilter, filter);

        assertEquals(expectedFilteredEvents.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setStartDatePattern(LocalDateTime.of(2024, 7, 10, 12, 0));

        var actualFilteredUsers = eventStartDateFilter.apply(eventsToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}