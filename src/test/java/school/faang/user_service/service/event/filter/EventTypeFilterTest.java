package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventTypeFilterTest {
    private EventTypeFilter filter;
    private EventFilterDto filterDto;
    private Event event1, event2, event3;

    @BeforeEach
    void init() {
        filter = new EventTypeFilter();
        filterDto = new EventFilterDto();
        event1 = Event.builder().type(EventType.GIVEAWAY).build();
        event2 = Event.builder().type(EventType.POLL).build();
        event3 = Event.builder().type(EventType.GIVEAWAY).build();
    }

    @Test
    void isAcceptableBadDto() {
        assertFalse(filter.isAcceptable(filterDto));
    }

    @Test
    void isAcceptableGoodDto() {
        filterDto.setType(EventType.GIVEAWAY);
        assertTrue(filter.isAcceptable(filterDto));
    }

    @Test
    void apply() {
        filterDto.setType(EventType.GIVEAWAY);
        Event[] expected = new Event[]{event1, event3};
        Stream<Event> out = filter.apply(Stream.of(event1, event2, event3), filterDto);
        assertArrayEquals(expected, out.toArray());
    }
}