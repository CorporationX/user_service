package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

public class EventTitileFilterTest {
    private static EventTitleFilter titleFilter;
    private static EventFilterDto filterDto;
    private static List<Event> eventList;
    private static Event event;

    @BeforeAll
    public static void init() {
        titleFilter = new EventTitleFilter();
        filterDto = createEventFilterDto("test1");
        event = createEvent("test1");

        eventList = List.of(event,
                createEvent("test2"),
                createEvent("test3"),
                event);

    }

    private static EventFilterDto createEventFilterDto(String eventStatus) {
        return EventFilterDto.builder().titlePattern(eventStatus).build();
    }

    private static Event createEvent(String title) {
        return Event.builder().title(title).build();
    }

    @Test
    public void checkApply() {
        List<Event> eventStream = Stream.of(event, event).toList();

        Assertions.assertEquals(eventStream, titleFilter.apply(eventList, filterDto).toList());
    }
}
