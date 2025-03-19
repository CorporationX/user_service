package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventLocationFilterTest {

    private final EventFilter filter = new EventLocationFilter();
    private EventFilterDto eventFilterDto;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    public void setUp() {
        eventFilterDto = new EventFilterDto();
        firstEvent = new Event();
        secondEvent = new Event();
    }

    @Test
    public void testIsApplicableWithNullLocation() {
        assertFalse(filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testIsApplicableWithBlankLocation() {
        eventFilterDto.setLocation("  ");
        assertFalse(filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testIsApplicableSuccessfully() {
        eventFilterDto.setLocation("Moscow");
        assertTrue(filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testApplyWithWrongCase() {
        eventFilterDto.setLocation("Moscow");
        firstEvent.setLocation("mOsCoW");
        secondEvent.setLocation("moscow");
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("moscow", result.get(1).getLocation());
    }

    @Test
    public void testApplyWithNoResults() {
        eventFilterDto.setLocation("Moscow");
        firstEvent.setLocation("Izhevsk");
        secondEvent.setLocation("Kirov");
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertEquals(0, result.size());
    }

    @Test
    public void testApplySuccessfully() {
        eventFilterDto.setLocation("Moscow");
        firstEvent.setLocation("Moscow");
        secondEvent.setLocation("Kirov");
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertNotNull(result);
        assertEquals("Moscow", result.get(0).getLocation());
        assertEquals(1, result.size());
    }
}
