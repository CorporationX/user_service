package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.OwnerPatternFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OwnerFilterTest {
    private EventFilterDto eventFilterDto;
    private EventFilter filter;
    private List<Event> events;

    @BeforeEach
    void setUp() {
        User userFirst = new User();
        userFirst.setUsername("userFirstEvent");
        Event eventFirst = new Event();
        eventFirst.setOwner(userFirst);
        User userSecond = new User();
        userSecond.setUsername("userSecondEvent");
        Event eventSecond = new Event();
        eventSecond.setOwner(userSecond);
        events = List.of(eventFirst, eventSecond);

        User ownerFilter = new User();
        ownerFilter.setUsername(userFirst.getUsername());
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setOwnerPattern(ownerFilter);
        filter = new OwnerPatternFilter();
    }

    @Test
    public void testApply(){
        // Arrange
        int sizeFilteredListExp = 1;

        // Act
        List<Event> filteredList = filter.apply(events.stream(), eventFilterDto).toList();
        int sizeFilteredListActual = filteredList.size();

        // Assert
        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(events.get(0), filteredList.get(0));
    }

    @Test
    public void testTest(){
        assertTrue(filter.test(events.get(0), eventFilterDto));
        assertFalse(filter.test(events.get(1), eventFilterDto));
    }
}
