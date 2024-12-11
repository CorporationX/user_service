package school.faang.user_service.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserTest {
    @Mock
    private Event mockEvent;

    @Mock
    private User mockMentor;

    @Mock
    private Goal mockGoal;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setParticipatedEvents(new ArrayList<>());
        user.setOwnedEvents(new ArrayList<>());
        user.setSettingGoals(new ArrayList<>());
        user.setGoals(new ArrayList<>());
        user.setMentors(new ArrayList<>());
    }

    @Test
    void testRemoveParticipatedEventWithUserHasEvent() {
        user.getParticipatedEvents().add(mockEvent);
        assertTrue(user.getParticipatedEvents().contains(mockEvent));

        user.removeParticipatedEvent(mockEvent);

        assertFalse(user.getParticipatedEvents().contains(mockEvent));
    }

    @Test
    void testRemoveOwnedEvent() {
        user.getOwnedEvents().add(mockEvent);
        assertTrue(user.getOwnedEvents().contains(mockEvent));

        user.removeOwnedEvent(mockEvent);

        assertFalse(user.getOwnedEvents().contains(mockEvent));
    }

    @Test
    void testToLogString() {
        user.setUsername("testUser");
        user.setId(1L);

        String logString = user.toLogString();
        assertEquals("User testUser with id 1", logString);
    }

    @Test
    void testRemoveAllGoals() {
        user.getSettingGoals().add(mockGoal);
        user.getGoals().add(mockGoal);

        assertTrue(user.getSettingGoals().contains(mockGoal));
        assertTrue(user.getGoals().contains(mockGoal));

        user.removeAllGoals();

        assertTrue(user.getSettingGoals().isEmpty());
        assertTrue(user.getGoals().isEmpty());
    }

    @Test
    void testRemoveMentor() {
        user.getMentors().add(mockMentor);
        assertTrue(user.getMentors().contains(mockMentor));

        user.removeMentor(mockMentor);

        assertFalse(user.getMentors().contains(mockMentor));
    }

}

