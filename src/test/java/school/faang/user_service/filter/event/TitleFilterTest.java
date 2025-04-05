package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TitleFilterTest {

    private TitleFilter titleFilter;
    private EventFilterDto filter;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        titleFilter = new TitleFilter();
        filter = EventFilterDto.builder().build();
        firstEvent = new Event();
        secondEvent = new Event();
    }

    @Test
    void testIsApplicableWithNullTitle() {
        filter.setTitlePattern(null);

        assertFalse(titleFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithBlankTitle() {
        filter.setTitlePattern("    ");

        assertFalse(titleFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValidTitle() {
        filter.setTitlePattern("Title");

        assertTrue(titleFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithNonMatchingTitle() {
        filter.setTitlePattern("nonexistent");
        firstEvent.setTitle("The first event");
        secondEvent.setTitle("The second event");
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = titleFilter.apply(events, filter).toList();

        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testApplyWithMatchingTitle() {
        filter.setTitlePattern("FiRsT");
        firstEvent.setTitle("The first event");
        secondEvent.setTitle("The second event");
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = titleFilter.apply(events, filter).toList();

        assertEquals(1, filteredEvents.size());
    }
}