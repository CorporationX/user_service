package school.faang.user_service.entity.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.User;
import school.faang.user_service.model.event.Event;
import school.faang.user_service.model.event.EventStatus;
import school.faang.user_service.model.event.EventType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventTest {

    private Event event;

    @Mock
    private User mockOwner;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("A description")
                .owner(mockOwner)
                .location("New York")
                .maxAttendees(100)
                .type(EventType.MEETING)
                .status(EventStatus.COMPLETED)
                .build();
    }

    @Test
    void testIsSameOwnerById_WithMatchingId() {
        Long ownerId = 1L;
        when(mockOwner.getId()).thenReturn(ownerId); // Mock owner ID to return 1L

        assertTrue(event.isSameOwnerById(ownerId), "Owner ID should match");
    }

    @Test
    void testIsSameOwnerById_WithNonMatchingId() {
        Long ownerId = 2L;
        when(mockOwner.getId()).thenReturn(1L); // Mock owner ID to return 1L

        assertFalse(event.isSameOwnerById(ownerId), "Owner ID should not match");
    }

    @Test
    void testIsSameOwnerById_WithNullId() {
        assertFalse(event.isSameOwnerById(null), "Owner ID should not match null");
    }

    @Test
    void testIsSameTitle_WithMatchingTitle() {
        assertTrue(event.isSameTitle("Test Event"), "Event title should match");
    }

    @Test
    void testIsSameTitle_WithNonMatchingTitle() {
        assertFalse(event.isSameTitle("Other Event"), "Event title should not match");
    }

    @Test
    void testIsSameTitle_WithBlankTitle() {
        assertFalse(event.isSameTitle(""), "Blank title should return false");
    }

    @Test
    void testIsSameLocation_WithMatchingLocation() {
        assertTrue(event.isSameLocation("New York"), "Event location should match");
    }

    @Test
    void testIsSameLocation_WithNonMatchingLocation() {
        assertFalse(event.isSameLocation("Los Angeles"), "Event location should not match");
    }

    @Test
    void testIsSameLocation_WithBlankLocation() {
        assertFalse(event.isSameLocation(""), "Blank location should return false");
    }

    @Test
    void testToLogString() {
        Long ownerId = 1L;
        when(mockOwner.getId()).thenReturn(ownerId);

        String expectedLogString = "Event(id=1, title=Test Event, owner=1)";
        assertEquals(expectedLogString, event.toLogString(), "Log string should match expected format");
    }
}