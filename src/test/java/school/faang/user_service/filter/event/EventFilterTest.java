package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class EventFilterTest {
    private List<EventDto> createEventDtoList() {
        EventDto event1 = new EventDto();
        event1.setId(1L);
        event1.setTitle("Title");
        EventDto event2 = new EventDto();
        event2.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        event2.setEndDate(LocalDate.of(2021, 1, 1).atStartOfDay());
        EventDto event3 = new EventDto();
        event3.setMaxAttendees(5);
        return List.of(event1, event2, event3);
    }

    @Test
    void getEventsByFilter_IdTitleFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setId(1L);
        filter.setTitle("Title");

        List<EventFilter> eventFilters = List.of(new EventIdFilter(), new EventTitleFilter());
        List<EventDto> eventDtos = new ArrayList<>(createEventDtoList());

        eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .forEach(eventFilter -> eventFilter.apply(eventDtos, filter));

        assertNotNull(eventDtos);
        assertEquals(1, eventDtos.size());
        assertEquals(1, eventDtos.get(0).getId());
    }

    @Test
    void getEventsByFilter_DateFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setLaterThanStartDate(LocalDate.of(2019, 6, 1).atStartOfDay());
        filter.setEarlierThanEndDate(LocalDate.of(2022, 7, 20).atStartOfDay());

        List<EventFilter> eventFilters = List.of(new EventDateFilter());
        List<EventDto> eventDtos = new ArrayList<>(createEventDtoList());

        eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .forEach(eventFilter -> eventFilter.apply(eventDtos, filter));

        assertNotNull(eventDtos);
        assertEquals(1, eventDtos.size());
    }

    @Test
    void getEventsByFilter_maxAttendeesFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setLessThanMaxAttendees(2);

        List<EventFilter> eventFilters = List.of(
                new EventMaxAttendeesFilter());
        List<EventDto> eventDtos = new ArrayList<>(createEventDtoList());

        eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .forEach(eventFilter -> eventFilter.apply(eventDtos, filter));

        assertNotNull(eventDtos);
        assertEquals(2, eventDtos.size());
    }
}
