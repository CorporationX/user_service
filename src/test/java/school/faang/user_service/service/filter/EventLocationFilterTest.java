package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class EventLocationFilterTest {
    private static EventLocationFilter locationFilter;
    private static List<Event> eventList;
    private static EventFilterDto filterDto;
    private static Event event;


    @BeforeAll
    public static void init() {
        locationFilter = new EventLocationFilter();
        String resultLocation = "Moscow";

        filterDto = EventFilterDto.builder().locationPattern(resultLocation).build();
        event = createEvent(resultLocation);
        eventList = List.of(createEvent("Paris"),
                event,
                createEvent("London"),
                event);
    }

    private static Event createEvent(String text) {
        return Event.builder().location(text).build();
    }

    @Test
    public void checkApplyContains() {
        List<Event> resultList = Stream.of(event, event).toList();

        Assertions.assertEquals(resultList, locationFilter.apply(eventList, filterDto).toList());
    }
}
