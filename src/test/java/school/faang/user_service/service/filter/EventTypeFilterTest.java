package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.List;
import java.util.stream.Stream;

public class EventTypeFilterTest {
    private static EventTypeFilter typeFilter;
    private static EventFilterDto filterDto;
    private static List<Event> eventList;
    private static Event event;

    @BeforeAll
    public static void init() {
        typeFilter = new EventTypeFilter();
        filterDto = createEventFilterDto(EventType.POLL.getMessage());
        event = createEvent(EventType.POLL);

        eventList = List.of(event,
                createEvent(EventType.GIVEAWAY),
                createEvent(EventType.MEETING),
                event);
    }

    private static EventFilterDto createEventFilterDto(String eventStatus) {
        return EventFilterDto.builder().typePattern(eventStatus).build();
    }

    private static Event createEvent(EventType eventType) {
        return Event.builder().type(eventType).build();
    }

    @Test
    public void checkApply() {
        List<Event> eventStream = Stream.of(event, event).toList();

        Assertions.assertEquals(eventStream, typeFilter.apply(eventList, filterDto).toList());
    }
}
