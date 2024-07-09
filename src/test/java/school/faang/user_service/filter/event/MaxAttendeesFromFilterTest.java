package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.MaxAttendeesFromPatternFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaxAttendeesFromFilterTest {
    private EventFilterDto eventFilterDto;
    private EventFilter filter;
    private List<Event> events;

    @BeforeEach
    void setUp() {
        int maxAttendeesFilterFrom = 3;
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setMaxAttendeesFromPattern(maxAttendeesFilterFrom);
        filter = new MaxAttendeesFromPatternFilter();

        int maxAttendeesFirstEvent = 2;
        int maxAttendeesSecondEvent = 4;
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setMaxAttendees(maxAttendeesFirstEvent);
        eventSecond.setMaxAttendees(maxAttendeesSecondEvent);
        events = List.of(eventFirst, eventSecond);
    }

    @Test
    public void testApply(){
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filter.apply(events.stream(), eventFilterDto).toList();
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(events.get(1), filteredList.get(0));
    }

    @Test
    public void testTest(){
        assertFalse(filter.test(events.get(0), eventFilterDto));
        assertTrue(filter.test(events.get(1), eventFilterDto));
    }
}
