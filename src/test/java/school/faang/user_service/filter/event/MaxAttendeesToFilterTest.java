package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.MaxAttendeesToPatternFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaxAttendeesToFilterTest {
    private EventFilterDto eventFilterDto;
    private List<Event> events;
    private EventFilter filter;

    @BeforeEach
    void setUp() {
        int maxAttendeesFilterTo = 3;
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setMaxAttendeesToPattern(maxAttendeesFilterTo);
        filter = new MaxAttendeesToPatternFilter();

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
        assertEquals(events.get(0), filteredList.get(0));
    }

    @Test
    public void testTest(){
        assertTrue(filter.test(events.get(0), eventFilterDto));
        assertFalse(filter.test(events.get(1), eventFilterDto));
    }
}
