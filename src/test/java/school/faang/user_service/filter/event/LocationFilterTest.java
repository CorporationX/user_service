package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.LocationPatternFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationFilterTest {
    private EventFilterDto eventFilterDto;
    private EventFilter filter;
    private List<Event> events;

    @BeforeEach
    void setUp() {
        String locationFilter = "Russian";
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setLocationPattern(locationFilter);
        filter = new LocationPatternFilter();

        String locationFirstEvent = "Russian Federation";
        String locationSecondEvent = "France";
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setLocation(locationFirstEvent);
        eventSecond.setLocation(locationSecondEvent);
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
