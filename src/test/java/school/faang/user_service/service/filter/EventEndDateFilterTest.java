package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public class EventEndDateFilterTest {
    private static final LocalDateTime dateTime = LocalDateTime.now();
    private static EventEndDateFilter endDateFilter;
    private static EventFilterDto filterNowDto;
    private static EventFilterDto filterBeforeDto;
    private static List<Event> eventList;
    private static Event eventEndDateNow;
    private static Event eventEndDateBefore;

    @BeforeAll
    public static void init() {
        endDateFilter = new EventEndDateFilter();
        filterNowDto = createEventMinusFilterNow();
        filterBeforeDto = createEventMinusFilterBefore();
        eventEndDateNow = createEventMinusNow();
        eventEndDateBefore = createEventMinus();

        eventList = List.of(eventEndDateNow, eventEndDateNow, eventEndDateBefore);
    }

    private static Event createEventMinus() {
        return Event.builder().endDate((dateTime.minusDays(90))).build();
    }

    private static Event createEventMinusNow() {
        return Event.builder().endDate(dateTime).build();
    }

    private static EventFilterDto createEventMinusFilterBefore() {
        return EventFilterDto.builder().endDatePattern(dateTime.minusDays(1)).build();
    }

    private static EventFilterDto createEventMinusFilterNow() {
        return EventFilterDto.builder().endDatePattern(dateTime).build();
    }

    @Test
    public void checkApplyIsBefore() {
        Assertions.assertEquals(eventList,
                endDateFilter.apply(eventList, filterNowDto).toList());
    }

    @Test
    public void checkApply() {
        Assertions.assertEquals(List.of(eventEndDateBefore),
                endDateFilter.apply(eventList, filterBeforeDto).toList());
    }
}
