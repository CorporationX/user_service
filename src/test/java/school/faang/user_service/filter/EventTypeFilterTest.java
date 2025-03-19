package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventTypeFilterTest {

    private final EventFilter filter = new EventTypeFilter();
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
    public void testIsApplicableWithNullType() {
        assertFalse(filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testIsApplicableSuccessfully() {
        eventFilterDto.setEventType(EventType.GIVEAWAY);
        assertTrue(filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testApplyWithNoResults() {
        eventFilterDto.setEventType(EventType.GIVEAWAY);
        firstEvent.setType(EventType.MEETING);
        secondEvent.setType(EventType.WEBINAR);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertEquals(0, result.size());
    }

    @Test
    public void testApplySuccessfully() {
        eventFilterDto.setEventType(EventType.GIVEAWAY);
        firstEvent.setType(EventType.GIVEAWAY);
        secondEvent.setType(EventType.MEETING);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertNotNull(result);
        assertEquals(EventType.GIVEAWAY, result.get(0).getType());
        assertEquals(1, result.size());
    }
}
