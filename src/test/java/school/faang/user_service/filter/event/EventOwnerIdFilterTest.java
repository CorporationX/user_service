package school.faang.user_service.filter.event;

import com.amazonaws.services.s3.model.Owner;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EventOwnerIdFilterTest {
    @InjectMocks
    private EventOwnerIdFilter filter;

    @Test
    public void testIsApplicableTrue() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setOwnerId(1L);

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
        filterDto.setOwnerId(1L);
        User owner1 = User.builder().id(1L).build();
        User owner2 = User.builder().id(2L).build();
        Event event1 = Event.builder().id(1L).owner(owner1).build();
        Event event2 = Event.builder().id(2L).owner(owner2).build();
        Event event3 = Event.builder().id(3L).owner(owner1).build();

        List<Event> result = filter.apply(Stream.of(event1, event2, event3), filterDto).toList();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
    }
}