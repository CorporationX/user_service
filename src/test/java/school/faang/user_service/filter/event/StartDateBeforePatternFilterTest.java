package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.StartDateBeforePatternFilter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
public class StartDateBeforePatternFilterTest {
    private EventFilterDto eventFilterDto;
    private List<Event> events;
    private EventFilter filter;

    @BeforeEach
    void setUp() {
        LocalDateTime startDateAfterFilter = LocalDateTime.of(2024, 7, 5, 13, 0);
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setStartDateBeforePattern(startDateAfterFilter);
        filter = new StartDateBeforePatternFilter();

        LocalDateTime startDateFirst = LocalDateTime.of(2024, 7, 5, 12, 0);
        LocalDateTime startDateSecond = LocalDateTime.of(2024, 7, 7, 12, 0);
        Event eventFirst = new Event();
        eventFirst.setStartDate(startDateFirst);
        Event eventSecond = new Event();
        eventSecond.setStartDate(startDateSecond);
        events = List.of(eventFirst, eventSecond);
    }

    @Test
    public void testSupply() {
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filter.apply(events.stream(), eventFilterDto).toList();
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(events.get(0), filteredList.get(0));
    }

    @Test
    public void testTest() {
        assertTrue(filter.test(events.get(0), eventFilterDto));
        assertFalse(filter.test(events.get(1), eventFilterDto));
    }
}
