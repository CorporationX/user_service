package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.List;
import java.util.stream.Stream;

public class EventStatusFilterTest {
    private static EventStatusFilter statusFilter;
    private static EventFilterDto filterDto;
    private static List<Event> eventList;
    private static Event event;

    @BeforeAll
    public static void init() {
        statusFilter = new EventStatusFilter();
        filterDto = createEventFilterDto(EventStatus.COMPLETED.getMessage());
        event = createEvent(EventStatus.COMPLETED);

        eventList = List.of(event,
                createEvent(EventStatus.CANCELED),
                createEvent(EventStatus.PLANNED),
                event);

    }

    private static EventFilterDto createEventFilterDto(String eventStatus) {
        return EventFilterDto.builder().statusPattern(eventStatus).build();
    }

    private static Event createEvent(EventStatus eventStatus) {
        return Event.builder().status(eventStatus).build();
    }

    @Test
    public void checkApply() {
        List<Event> eventStream = Stream.of(event, event).toList();

        Assertions.assertEquals(eventStream, statusFilter.apply(eventList, filterDto).toList());
    }
}
