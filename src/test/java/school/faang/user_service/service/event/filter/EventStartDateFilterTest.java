package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventStartDateFilterTest {
    private EventStartDateFilter filter;
    private EventFilterDto filterDto;
    private Event event1, event2, event3;

    @BeforeEach
    void init() {
        filter = new EventStartDateFilter();
        filterDto = new EventFilterDto();
        event1 = Event.builder().startDate(LocalDateTime.now().plusDays(1)).build();
        event2 = Event.builder().startDate(LocalDateTime.now()).build();
        event3 = Event.builder().startDate(LocalDateTime.now().plusDays(2)).build();
    }

    @Test
    void isAcceptableBadDto() {
        assertFalse(filter.isAcceptable(filterDto));
    }

    @Test
    void isAcceptableGoodDto() {
        filterDto.setStartDate(LocalDateTime.now());
        assertTrue(filter.isAcceptable(filterDto));
    }

    @Test
    void apply() {
        filterDto.setStartDate(LocalDateTime.now());
        Event[] expected = new Event[]{event1, event3};
        Stream<Event> out = filter.apply(Stream.of(event1, event2, event3), filterDto);
        assertArrayEquals(expected, out.toArray());
    }
}