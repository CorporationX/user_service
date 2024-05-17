package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.service.event.filter.EventStatusFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EvenStatusFilterTest {
    EventStatusFilter eventStatusFilter;

    @BeforeEach
    public void setUp() {
        eventStatusFilter = new EventStatusFilter();
    }

    @Test
    public void testIsApplicable_NullStatus() {
        assertFalse(eventStatusFilter.isApplicable(new EventFilterDto()));
    }

    @Test
    public void testIsApplicable_WithStatus() {
        assertTrue(eventStatusFilter.isApplicable(EventFilterDto.builder().status(EventStatus.IN_PROGRESS).build()));
    }

    @Test
    public void testApply() {
        Event event1 = Event.builder().status(EventStatus.COMPLETED).build();
        Event event2 = Event.builder().status(EventStatus.PLANNED).build();
        Event event3 = Event.builder().status(EventStatus.COMPLETED).build();

        Stream<Event> streams = Stream.of(event1, event2, event3);
        Stream<Event> expectedStreams = Stream.of(event1, event3);

        EventFilterDto eventFilterDto = EventFilterDto.builder().status(EventStatus.COMPLETED).build();

        Stream<Event> resStream = eventStatusFilter.apply(streams, eventFilterDto);
        assertEquals(expectedStreams.toList(), resStream.toList());
    }
}