package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.EndDateAfterPatternFilter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
public class EndDateAfterFilterTest {
    private EventFilterDto eventFilterDto;
    private List<Event> events;
    private EventFilter filter;

    @BeforeEach
    void setUp() {
        LocalDateTime endDateAfterFilter = LocalDateTime.of(2024, 7, 6, 13, 0);
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setEndDateAfterPattern(endDateAfterFilter);
        filter = new EndDateAfterPatternFilter();

        LocalDateTime endDateFirst = LocalDateTime.of(2024, 7, 5, 12, 0);
        LocalDateTime endDateSecond = LocalDateTime.of(2024, 7, 7, 12, 0);
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setEndDate(endDateFirst);
        eventSecond.setEndDate(endDateSecond);
        events = List.of(eventFirst, eventSecond);
    }

    @Test
    public void testApply() {
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filter.apply(events.stream(), eventFilterDto).toList();
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(events.get(1), filteredList.get(0));
    }

    @Test
    public void testTest() {
        assertFalse(filter.test(events.get(0), eventFilterDto));
        assertTrue(filter.test(events.get(1), eventFilterDto));
    }

}
