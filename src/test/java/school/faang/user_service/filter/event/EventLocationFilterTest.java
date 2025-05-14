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
class EventLocationFilterTest {

    @InjectMocks
    private EventLocationFilter filter;

    @Test
    public void testIsApplicableTrue() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setLocation("location");

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
        filterDto.setLocation("Copenhagen");
        Event event1 = Event.builder().id(1L).location("Copenhagen").build();
        Event event2 = Event.builder().id(2L).location("Aarhus").build();
        Event event3 = Event.builder().id(3L).location("copenhagen").build();

        List<Event> result = filter.apply(Stream.of(event1, event2, event3), filterDto).toList();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
    }


    @Test
    void testApplyEventLocationIsNullSkipsIt() {
        Event event1 = Event.builder().id(1L).location(null).build();
        Event event2 = Event.builder().id(2L).location("Copenhagen").build();

        EventFilterDto dto = new EventFilterDto();
        dto.setLocation("Copenhagen");

        List<Event> result = filter.apply(Stream.of(event1, event2), dto).toList();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

}