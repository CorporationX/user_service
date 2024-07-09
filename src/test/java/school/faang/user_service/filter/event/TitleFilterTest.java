package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.TitlePatternFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TitleFilterTest {
    private EventFilterDto eventFilterDto;
    private List<Event> events;
    private EventFilter filter;

    @BeforeEach
    void setUp() {
        String titleFilter = "first";
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setTitlePattern(titleFilter);
        filter = new TitlePatternFilter();

        String titleFirstEvent = "titleFirstEvent";
        String titleSecondEvent = "titleSecondEvent";
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setTitle(titleFirstEvent);
        eventSecond.setTitle(titleSecondEvent);
        events = List.of(eventFirst, eventSecond);
    }

    @Test
    public void testSupply(){
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filter.apply(events.stream(), eventFilterDto)
                .toList();
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
