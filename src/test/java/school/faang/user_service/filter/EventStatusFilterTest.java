package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventStatusFilterTest {

    private final EventFilter filter = new EventStatusFilter();
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
    public void testIsApplicableWithNullStatus() {
        assertFalse(filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testIsApplicableSuccessfully() {
        eventFilterDto.setEventStatus(EventStatus.IN_PROGRESS);
        assertTrue(filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testApplyWithNoResults() {
        eventFilterDto.setEventStatus(EventStatus.IN_PROGRESS);
        firstEvent.setStatus(EventStatus.CANCELED);
        secondEvent.setStatus(EventStatus.COMPLETED);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertEquals(0, result.size());
    }

    @Test
    public void testApplySuccessfully() {
        eventFilterDto.setEventStatus(EventStatus.IN_PROGRESS);
        firstEvent.setStatus(EventStatus.IN_PROGRESS);
        secondEvent.setStatus(EventStatus.COMPLETED);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertNotNull(result);
        assertEquals(EventStatus.IN_PROGRESS, result.get(0).getStatus());
        assertEquals(1, result.size());
    }
}
