package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventOwnerIdFilter;

import java.util.List;
import java.util.stream.Stream;

public class EventOwnerIdFilterTest {
    private EventFilterDto eventFilterDto = EventFilterDto.builder().ownerId(1L).build();
    private EventOwnerIdFilter eventOwnerIdFilter = new EventOwnerIdFilter();
    private List<Event> events = List.of(
            Event.builder().owner(User.builder().id(1).build()).build(),
            Event.builder().owner(User.builder().id(2).build()).build(),
            Event.builder().owner(User.builder().id(1).build()).build(),
            Event.builder().owner(User.builder().id(3).build()).build(),
            Event.builder().owner(User.builder().id(3).build()).build()
    );

    @Test
    public void testApply() {
        Stream<Event> eventStream = events.stream();
        List<Event> actualEvents = eventOwnerIdFilter.apply(eventStream, eventFilterDto).toList();
        Assertions.assertEquals(2, actualEvents.size());
        actualEvents.stream()
                .forEach(event -> Assertions.assertEquals(event.getOwner().getId(), eventFilterDto.getOwnerId()));
    }
}
