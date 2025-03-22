package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventEndDateFilterTest {

    private static final LocalDateTime END_DATE =
            LocalDateTime.of(2020, 10, 10, 10, 10);
    private final EventFilter filter = new EventEndDateFilter();
    private EventFilterDto eventFilterDto;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    public void setUp() {
        eventFilterDto = new EventFilterDto();
        firstEvent = new Event();
        secondEvent = new Event();
        firstEvent.setEndDate(LocalDateTime.now());
    }

    @Test
    public void testIsApplicableWithNullEventFilterDto() {
        boolean result = filter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessfully() {
        eventFilterDto.setEndDate(LocalDateTime.now());
        boolean result = filter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void testApplyWithWrongEndTime() {
        secondEvent.setEndDate(LocalDateTime.now());
        eventFilterDto.setEndDate(END_DATE);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertEquals(0, result.size());
    }

    @Test
    public void testApplySuccessfully() {
        secondEvent.setEndDate(END_DATE);
        eventFilterDto.setEndDate(END_DATE);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(END_DATE, result.get(0).getEndDate());
    }
}
