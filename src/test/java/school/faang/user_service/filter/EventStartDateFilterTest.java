package school.faang.user_service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filters.event.EventStartDateFilter;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

public class EventStartDateFilterTest {
    private EventFilterDto eventFilterDto = EventFilterDto.builder()
            .startDate(LocalDateTime.of(2020, Month.APRIL, 3, 16, 30))
            .build();
    private EventStartDateFilter eventStartDateFilter = new EventStartDateFilter();
    private List<Event> events = List.of(
            Event.builder().startDate(LocalDateTime.of(2021, Month.APRIL, 3, 16, 30)).build(),
            Event.builder().startDate(LocalDateTime.of(2022, Month.APRIL, 3, 16, 30)).build(),
            Event.builder().startDate(LocalDateTime.of(2023, Month.APRIL, 3, 16, 30)).build(),
            Event.builder().startDate(LocalDateTime.of(2019, Month.APRIL, 3, 16, 30)).build(),
            Event.builder().startDate(LocalDateTime.of(2015, Month.APRIL, 3, 16, 30)).build()
    );

    @Test
    public void testApply() {
        Stream<Event> eventStream = events.stream();
        List<Event> actualEvents = eventStartDateFilter.apply(eventStream, eventFilterDto).toList();
        Assertions.assertEquals(3, actualEvents.size());
        actualEvents.stream()
                .forEach(event -> Assertions.assertTrue(event.getStartDate().isAfter(eventFilterDto.getStartDate())));
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(eventStartDateFilter.isApplicable(eventFilterDto));
        Assertions.assertFalse(eventStartDateFilter.isApplicable(EventFilterDto.builder().build()));
    }
}
