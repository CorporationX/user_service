package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.impl.DescriptionPatternFilter;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DescriptionFilterTest {
    private EventFilterDto eventFilterDto;
    private List<Event> events;
    private EventFilter filter;


    @BeforeEach
    void setUp() {
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setDescriptionPattern("filter");
        filter = new DescriptionPatternFilter();

        String descriptionFirstEvent = "some description1";
        String descriptionSecondEvent = "some description2 filter";
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setDescription(descriptionFirstEvent);
        eventSecond.setDescription(descriptionSecondEvent);
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
