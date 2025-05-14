package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EvenTypeFilterTest {
    @InjectMocks
    private EvenTypeFilter filter;

    @Test
    public void testIsApplicableTrue() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setEventType("workout");

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
        filterDto.setEventType("meeting");
        EventType type1 = EventType.MEETING;
        EventType type2 = EventType.POLL;
        Event event1 = Event.builder().id(1L).type(type1).build();
        Event event2 = Event.builder().id(2L).type(type2).build();

        List<Event> result = filter.apply(List.of(event1, event2).stream(), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testApplyEventFilterWithNullEventTypeSkipsIt() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setStatus("poll");
        EventType type1 = EventType.MEETING;
        EventType type2 = EventType.POLL;
        Event event1 = Event.builder().id(1L).type(type1).build();
        Event event2 = Event.builder().id(2L).type(type2).build();
        event2.setType(null);

        List<Event> result = filter.apply(List.of(event1, event2).stream(), filterDto).toList();

        assertEquals(0, result.size());
    }
}