package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventStatusFilterTest {
    private EventStatusFilter filter;
    private EventFilterDto filterDto;
    private Event event1, event2, event3;

    @BeforeEach
    void init() {
        filter = new EventStatusFilter();
        filterDto = new EventFilterDto();
        event1 = Event.builder().status(EventStatus.PLANNED).build();
        event2 = Event.builder().status(EventStatus.IN_PROGRESS).build();
        event3 = Event.builder().status(EventStatus.PLANNED).build();
    }

    @Test
    void isAcceptableBadDto() {
        assertFalse(filter.isAcceptable(filterDto));
    }

    @Test
    void isAcceptableGoodDto() {
        filterDto.setStatus(EventStatus.PLANNED);
        assertTrue(filter.isAcceptable(filterDto));
    }

    @Test
    void apply() {
        filterDto.setStatus(EventStatus.PLANNED);
        Event[] expected = new Event[]{event1, event3};
        Stream<Event> out = filter.apply(Stream.of(event1, event2, event3), filterDto);
        assertArrayEquals(expected, out.toArray());
    }
}