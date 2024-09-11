package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.DeactivateUserService;

import java.util.ArrayList;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class DeactivateUserServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private DeactivateUserService deactivateUserService;

    private User user;
    private Goal firstGoal;
    private Goal secondGoal;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setGoals(new ArrayList<>());
        user.setOwnedEvents(new ArrayList<>());

        firstGoal = new Goal();
        firstGoal.setId(1L);
        firstGoal.setUsers(new ArrayList<>(Arrays.asList(user)));

        secondGoal = new Goal();
        secondGoal.setId(2L);
        secondGoal.setUsers(new ArrayList<>(Arrays.asList(new User())));

        firstEvent = new Event();
        firstEvent.setId(1L);

        secondEvent = new Event();
        secondEvent.setId(2L);
    }

    @Test
    void stopUserActivitiesTest() {
        user.setGoals(Arrays.asList(firstGoal, secondGoal));
        user.setOwnedEvents(Arrays.asList(firstEvent, secondEvent));

        User result = deactivateUserService.stopUserActivities(user);

        assertEquals(user, result);
        assertTrue(user.getGoals().isEmpty());
        verify(goalRepository).deleteById(firstGoal.getId());
        assertTrue(user.getOwnedEvents().isEmpty());
        verify(eventRepository).deleteById(firstEvent.getId());
        verify(eventRepository).deleteById(secondEvent.getId());
    }

    @Test
    void stopGoalsTest_OneUserInGoals() {
        user.setGoals(Arrays.asList(firstGoal));
        deactivateUserService.stopGoals(user);
        assertTrue(user.getGoals().isEmpty());
        verify(goalRepository).deleteById(firstGoal.getId());
    }

    @Test
    void stopGoalsTest_SomeUsersInGoals() {
        user.setGoals(Arrays.asList(secondGoal));
        deactivateUserService.stopGoals(user);
        assertEquals(0, user.getGoals().size());
    }

    @Test
    void testStopEvents_ShouldDeleteAllOwnedEvents() {
        user.setOwnedEvents(Arrays.asList(firstEvent, secondEvent));
        deactivateUserService.stopEvents(user);
        assertTrue(user.getOwnedEvents().isEmpty());
        verify(eventRepository).deleteById(firstEvent.getId());
        verify(eventRepository).deleteById(secondEvent.getId());
    }
}