package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.filter.EventStartDateFilter;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventStartDateFilterTest {

    private EventStartDateFilter eventStartDateFilter;

    @BeforeEach
    public void setUp() {
        eventStartDateFilter = new EventStartDateFilter();
    }

    @Test
    public void testIsApplicable_NullStartDate() {
        assertFalse(eventStartDateFilter.isApplicable(new EventFilterDto()));
    }

    @Test
    public void testIsApplicable_WithStartDate() {
        assertTrue(eventStartDateFilter.isApplicable(EventFilterDto.builder().startDate(LocalDateTime.MAX).build()));
    }

    @Test
    public void testApply() {
        Event event1 = Event.builder().startDate(LocalDateTime.MAX).build();
        Event event2 = Event.builder().startDate(LocalDateTime.MIN).build();
        Event event3 = Event.builder().startDate(LocalDateTime.MAX).build();

        Stream<Event> streams = Stream.of(event1, event2, event3);
        Stream<Event> expectedStreams = Stream.of(event1, event3);

        EventFilterDto eventFilterDto = EventFilterDto.builder().startDate(LocalDateTime.MAX).build();

        Stream<Event> resStream = eventStartDateFilter.apply(streams, eventFilterDto);
        assertEquals(expectedStreams.toList(), resStream.toList());
    }
}