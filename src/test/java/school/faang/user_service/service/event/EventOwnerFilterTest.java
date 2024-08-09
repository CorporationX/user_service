package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventOwnerFilterTest {
    private EventOwnerFilter filter = new EventOwnerFilter();
    private EventFilterDto eventFilterDto = new EventFilterDto();
    private long userId = 1L;
    private User user = User.builder()
            .id(userId)
            .build();
    private Event eventWithOwner = Event.builder()
            .owner(user)
            .build();
    private Stream<Event> events = Stream.of(eventWithOwner, new Event());

    @Test
    public void testIsApplicableWithoutOwner() {
        assertEquals(false, filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testIsApplicableWithOwner() {
        eventFilterDto.setOwnerId(userId);
        assertEquals(true, filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testApply() {
        eventFilterDto.setOwnerId(userId);
        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertTrue(result.contains(eventWithOwner), "Result contains true event");
        assertEquals(1, result.size());
    }

    @Test
    public void testApplyWithNullFilters() {
        eventFilterDto = null;

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> filter.apply(events, eventFilterDto));
        assertEquals("EventFilterDto can't be null", exception.getMessage());
    }
}
