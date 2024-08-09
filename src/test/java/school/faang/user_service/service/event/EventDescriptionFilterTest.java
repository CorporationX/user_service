package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventDescriptionFilterTest {
    private EventDescriptionFilter filter = new EventDescriptionFilter();
    private EventFilterDto eventFilterDto = new EventFilterDto();
    private Event eventWithDescription = Event.builder()
            .description("Event for IT-founders")
            .build();
    private Stream<Event> events = Stream.of(eventWithDescription, new Event());

    @Test
    public void testIsApplicableWithoutDescription() {
        assertEquals(false, filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testIsApplicableWithDescription() {
        eventFilterDto.setDescriptionPattern("IT");
        assertEquals(true, filter.isApplicable(eventFilterDto));
    }

    @Test
    public void testApply() {
        eventFilterDto.setDescriptionPattern("IT");
        List<Event> result = filter.apply(events, eventFilterDto).toList();

        assertTrue(result.contains(eventWithDescription), "Result contains true event");
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
