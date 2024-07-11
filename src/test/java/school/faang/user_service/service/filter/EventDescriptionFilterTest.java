package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

public class EventDescriptionFilterTest {
    private static EventDescriptionFilter descriptionFilter;
    private static EventFilterDto filterDto;
    private static List<Event> eventList;
    private static Event event;

    @BeforeAll
    public static void init() {
        descriptionFilter = new EventDescriptionFilter();
        filterDto = createEventFilterDto("test1");
        event = createEvent("test1");

        eventList = List.of(event,
                createEvent("test2"),
                createEvent("test3"),
                event);

    }

    private static EventFilterDto createEventFilterDto(String text) {
        return EventFilterDto.builder().descriptionPattern(text).build();
    }

    private static Event createEvent(String text) {
        return Event.builder().description(text).build();
    }

    @Test
    public void checkApply() {
        List<Event> eventStream = Stream.of(event, event).toList();

        Assertions.assertEquals(eventStream, descriptionFilter.apply(eventList, filterDto).toList());
    }
}
