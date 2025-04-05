package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OwnerFilterTest {

    private OwnerFilter ownerFilter;
    private EventFilterDto filter;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        ownerFilter = new OwnerFilter();
        filter = EventFilterDto.builder().build();
        firstEvent = new Event();
        secondEvent = new Event();
    }

    @Test
    void testIsApplicableWithNullOwner() {
        filter.setOwnerPattern(null);

        assertFalse(ownerFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithBlankOwner() {
        filter.setOwnerPattern("    ");

        assertFalse(ownerFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValidOwner() {
        filter.setOwnerPattern("Pasha");

        assertTrue(ownerFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithNonMatchingOwner() {
        filter.setOwnerPattern("Pasha");
        User firstOwner = mock(User.class);
        when(firstOwner.getUsername()).thenReturn("Masha");
        User secondOwner = mock(User.class);
        when(secondOwner.getUsername()).thenReturn("Dasha");
        firstEvent.setOwner(firstOwner);
        secondEvent.setOwner(secondOwner);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = ownerFilter.apply(events, filter).toList();

        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testApplyWithMatchingOwner() {
        filter.setOwnerPattern("Pasha");
        User firstOwner = mock(User.class);
        when(firstOwner.getUsername()).thenReturn("Pasha");
        User secondOwner = mock(User.class);
        when(secondOwner.getUsername()).thenReturn("Dasha");
        firstEvent.setOwner(firstOwner);
        secondEvent.setOwner(secondOwner);
        Stream<Event> events = Stream.of(firstEvent, secondEvent);

        List<Event> filteredEvents = ownerFilter.apply(events, filter).toList();

        assertEquals(1, filteredEvents.size());
        assertEquals("pasha", filteredEvents.get(0).getOwner().getUsername().toLowerCase());

    }
}