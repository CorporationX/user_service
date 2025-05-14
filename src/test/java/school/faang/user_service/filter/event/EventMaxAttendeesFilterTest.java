package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventMaxAttendeesFilterTest {
    @InjectMocks
    private EventMaxAttendeesFilter filter;

    @Test
     public void testIsApplicableFalse() {
        EventFilterDto dto = new EventFilterDto();
        boolean result = filter.isApplicable(dto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableTrue() {
        EventFilterDto dto = new EventFilterDto();
        dto.setMaxAttendees(10);
        boolean result = filter.isApplicable(dto);

        assertTrue(result);
    }

    @Test
    public void testApply() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setMaxAttendees(10);
        Event event1 = Event.builder().id(1L).maxAttendees(10).build();
        Event event2 = Event.builder().id(2L).maxAttendees(8).build();
        Event event3 = Event.builder().id(3L).maxAttendees(12).build();

        List<Event> result = filter.apply(Stream.of(event1, event2, event3), filterDto).toList();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testApplyEventMaxAttendeesIsNullSkipsIt() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setMaxAttendees(10);
        Event event1 = Event.builder().id(1L).build();
        event1.setMaxAttendees(null);
        Event event2 = Event.builder().id(2L).maxAttendees(8).build();

        List<Event> result = filter.apply(Stream.of(event1, event2), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    public void testApplyEventMacAttendeesIsZeroAndLessSkipsIt() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setMaxAttendees(10);
        Event event1 = Event.builder().id(1L).maxAttendees(0).build();
        Event event2 = Event.builder().id(2L).maxAttendees(8).build();
        Event event3 = Event.builder().id(3L).maxAttendees(-9).build();
        Event event4 = Event.builder().id(4L).maxAttendees(100).build();

        List<Event> result = filter.apply(Stream.of(event1, event2, event3, event4), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

}