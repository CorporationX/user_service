package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class EventStartDateFilterTest {
    private static final LocalDateTime dateTime = LocalDateTime.now();
    private static EventStartDateFilter startDateFilter;
    private static EventFilterDto filterNowDto;
    private static EventFilterDto filterAfterDto;
    private static List<Event> eventList;
    private static Event eventStartDateNow;
    private static Event eventStartDateAfter;

    @BeforeAll
    public static void init() {
        startDateFilter = new EventStartDateFilter();
        filterNowDto = createEventFilterNow();
        filterAfterDto = createEventPlusFilterAfter();
        eventStartDateAfter = createEventPlus();
        eventStartDateNow = createEventNow();
        eventList = List.of(eventStartDateNow,
                eventStartDateNow, eventStartDateAfter);
    }

    private static Event createEventNow() {
        return Event.builder().startDate(dateTime).build();
    }

    private static Event createEventPlus() {
        return Event.builder().startDate(dateTime.plusDays(90)).build();
    }

    private static EventFilterDto createEventFilterNow() {
        return EventFilterDto.builder().startDatePattern(dateTime).build();
    }

    private static EventFilterDto createEventPlusFilterAfter() {
        return EventFilterDto.builder().startDatePattern(dateTime.plusDays(90)).build();
    }

    @Test
    public void checkApplyIsAfter() {
        List<Event> eventStream = Stream.of(eventStartDateAfter).toList();

        Assertions.assertEquals(eventStream, startDateFilter.apply(eventList, filterAfterDto).toList());
    }

    @Test
    public void checkApply() {
        Assertions.assertEquals(eventList, startDateFilter.apply(eventList, filterNowDto).toList());
    }

}
