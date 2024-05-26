package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.filter.EventDescriptionFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventDescriptionFilterTest {

    private EventDescriptionFilter eventDescriptionFilter;

    @BeforeEach
    public void setUp() {
        eventDescriptionFilter = new EventDescriptionFilter();
    }

    @Test
    public void testIsApplicable_NullDescription() {
        assertFalse(eventDescriptionFilter.isApplicable(new EventFilterDto()));
    }

    @Test
    public void testIsApplicable_WithDescription() {
        assertTrue(eventDescriptionFilter.isApplicable(EventFilterDto.builder().description("Desc").build()));
    }

    @Test
    public void testApply() {
        Event event1 = Event.builder().description("Description1").build();
        Event event2 = Event.builder().description("Description2").build();
        Event event3 = Event.builder().description("Description1").build();

        Stream<Event> streams = Stream.of(event1, event2, event3);
        Stream<Event> expectedStreams = Stream.of(event1, event3);

        EventFilterDto eventFilterDto = EventFilterDto.builder().description("Description1").build();

        Stream<Event> resStream = eventDescriptionFilter.apply(streams, eventFilterDto);
        assertEquals(expectedStreams.toList(), resStream.toList());
    }
}