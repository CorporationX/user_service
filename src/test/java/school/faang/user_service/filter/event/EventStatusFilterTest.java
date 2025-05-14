package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventStatusFilterTest {

    @InjectMocks
    private EventStatusFilter filter;

    @Test
    public void testIsApplicableTrue() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setStatus("upcoming");

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableFalse() {
        EventFilterDto filterDto = new EventFilterDto();

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    public void testApply() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setStatus("CANCELED");
        EventStatus status1 = EventStatus.IN_PROGRESS;
        EventStatus status2 = EventStatus.CANCELED;

        Event event1 = Event.builder().id(1L).status(status1).build();
        Event event2 = Event.builder().id(2L).status(status2).build();

        List<Event> result = filter.apply(List.of(event1, event2).stream(), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    public void testApplyEventStatusFilterWithNullStatusSkipsIt() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setStatus("CANCELED");
        EventStatus status1 = EventStatus.IN_PROGRESS;
        EventStatus status2 = EventStatus.CANCELED;
        Event event1 = Event.builder().id(1L).status(status1).build();
        event1.setStatus(null);
        Event event2 = Event.builder().id(2L).status(status2).build();

        List<Event> result = filter.apply(List.of(event1, event2).stream(), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }
}