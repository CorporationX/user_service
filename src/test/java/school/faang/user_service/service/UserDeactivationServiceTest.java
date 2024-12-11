package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.MentorshipService;
import school.faang.user_service.service.user.UserDeactivationService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDeactivationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private GoalService goalService;

    @Mock
    private EventService eventService;

    @Mock
    private MentorshipService mentorshipService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Captor
    private ArgumentCaptor<User> captorUser;

    @InjectMocks
    private UserDeactivationService userDeactivationService;

    @Mock
    private User userToDeactivateMock;

    @Mock
    private Event eventOwnedMock1, eventOwnedMock2;

    @Mock
    private Goal goalOwnerMock1, goalOwnerMock2, goalSettingMock1, goalSettingMock2;

    private User user;
    private Event eventOwned1;
    private Event eventOwned2;
    private List<Goal> ownerGoals;
    private List<Goal> settingGoals;
    private User userToDeactivate;
    private List<User> attendees;
    private User attendee1;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .build();


        eventOwned1 = new Event();
        eventOwned1.setTitle("Event 1");
        eventOwned1.setAttendees(new ArrayList<>());


        eventOwned2 = new Event();
        eventOwned2.setTitle("Event 2");
        eventOwned2.setAttendees(new ArrayList<>());

        Goal goalOwner1 = new Goal();
        goalOwner1.setDescription("Goal 1");
        goalOwner1.setUsers(new ArrayList<>(List.of(userToDeactivateMock)));

        Goal goalOwner2 = new Goal();
        goalOwner2.setTitle("Goal 2");
        goalOwner2.setUsers(new ArrayList<>(List.of(userToDeactivateMock)));

        Goal goalSetting1 = new Goal();
        goalSetting1.setTitle("Setting Goal 1");
        goalSetting1.setUsers(new ArrayList<>());

        Goal goalSetting2 = new Goal();
        goalSetting2.setTitle("Setting Goal 2");
        goalSetting2.setUsers(new ArrayList<>());


        ownerGoals = new ArrayList<>();
        ownerGoals.add(goalOwner1);
        ownerGoals.add(goalOwner2);

        settingGoals = new ArrayList<>();
        settingGoals.add(goalSetting1);
        settingGoals.add(goalSetting2);


        List<Event> userEvents = new ArrayList<>();
        userEvents.add(eventOwned1);
        userEvents.add(eventOwned2);

        attendees = new ArrayList<>();
        attendees.add(attendee1);


        userToDeactivate = new User();
        userToDeactivate.setId(1L);
        userToDeactivate.setUsername("Naku");
        userToDeactivate.setActive(true);
        userToDeactivate.setOwnedEvents(userEvents);
        for (Event event : userToDeactivate.getOwnedEvents()) {
            event.setOwner(userToDeactivate);
        }
        userToDeactivate.setGoals(ownerGoals);
        attendees.add(userToDeactivate);
        List<Event> participatedEvents = new ArrayList<>();
        participatedEvents.add(Event.builder()
                .attendees(attendees)
                .build()
        );
        userToDeactivate.setParticipatedEvents(participatedEvents);

        for (Goal goal : settingGoals) {
            goal.getUsers().add(userToDeactivate);
        }

        userToDeactivate.setSettingGoals(settingGoals);

        attendee1 = new User();
        attendee1.setUsername("AAA");

        for (Event event : userEvents) {
            event.getAttendees().add(attendee1);
        }

        attendee1.setParticipatedEvents(userEvents);
    }

    @Test
    void testDeactivateWhenAllMethodsWork() {

        when(userService.getUserById(1L)).thenReturn(userToDeactivate);

        userDeactivationService.deactivateUser(1L);

        verify(goalService, times(2)).removeGoalsWithoutExecutingUsers(eq(ownerGoals));
        verify(goalService, times(2)).removeGoalsWithoutExecutingUsers(eq(settingGoals));
        verify(mentorshipService).stopMentorship(userToDeactivate);
        verify(eventService, times(1)).deleteAllEvents(anyList());
        verify(eventService, times(1)).updateAllEvents(anyList());
        verify(userService, times(1)).updateUser(userToDeactivate);
        verify(userService, times(1)).updateAllUsers(anyList());

        assertFalse(userToDeactivateMock.isActive(), "User should be deactivated");
    }

    @Test
    void testDeactivateUserWhenSavesCorrectUser() {
        when(userService.getUserById(1L))
                .thenReturn(userToDeactivate);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(ownerGoals);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(settingGoals);

        userDeactivationService.deactivateUser(1L);

        verify(userService, times(1)).updateUser(captorUser.capture());
        User deactevatedUser = captorUser.getValue();
        assertFalse(userToDeactivate.isActive());
        assertFalse(deactevatedUser.isActive());
        assertEquals(userToDeactivate, deactevatedUser);
    }

    @Test
    void testDeactivateUserWithExistingUserWhenAllDelCompleteWithConsistentData() {
        when(userService.getUserById(1L)).thenReturn(userToDeactivate);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(ownerGoals);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(settingGoals);
        when(userService.updateUser(userToDeactivate)).thenReturn(userToDeactivate);

        DeactivatedUserDto result = userDeactivationService.deactivateUser(userToDeactivate.getId());

        verify(mentorshipService).stopMentorship(userToDeactivate);
        verify(goalService, times(2)).removeGoalsWithoutExecutingUsers(anyList());
        verify(eventService, times(1)).deleteAllEvents(userToDeactivate.getOwnedEvents());
        assertFalse(userToDeactivate.isActive(), "User should be deactivated");
        assertFalse(attendee1.getParticipatedEvents().contains(eventOwned1));
        assertFalse(attendee1.getParticipatedEvents().contains(eventOwned1));
        assertTrue(userToDeactivate.getGoals().isEmpty());
        assertTrue(userToDeactivate.getSettingGoals().isEmpty());
        assertTrue(userToDeactivate.getOwnedEvents().isEmpty());
        assertNotNull(result);
    }

    @Test
    void testDeactivateUserWhenUserToDeactivateDto() {
        when(userService.getUserById(1L)).thenReturn(userToDeactivate);
        when(userService.updateUser(userToDeactivate)).thenReturn(userToDeactivate);

        DeactivatedUserDto result = userDeactivationService.deactivateUser(1L);

        verify(userService, times(1)).updateUser(userToDeactivate);

        assertFalse(userToDeactivate.isActive());
        assertFalse(result.active());
        assertNotNull(result);
        assertEquals(userToDeactivate.getId(), result.id());
        assertEquals(userToDeactivate.getUsername(), result.username());

    }

    @Test
    void testDeactivateUserWHenStopScheduledGoals() {
        List<Goal> ownedGoals = userToDeactivate.getGoals();
        List<Goal> settingGoals = userToDeactivate.getSettingGoals();

        when(userService.getUserById(1L)).thenReturn(userToDeactivate);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(ownedGoals);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(settingGoals);

        userDeactivationService.deactivateUser(userToDeactivate.getId());

        verify(goalService, times(2)).removeGoalsWithoutExecutingUsers(anyList());
        assertTrue(userToDeactivate.getGoals().isEmpty());
        assertTrue(userToDeactivate.getSettingGoals().isEmpty());
    }

    @Test
    void testDeactivateUserWithStopScheduledEvents() {
        List<Event> ownedEvents = userToDeactivate.getOwnedEvents();
        when(userService.getUserById(1L)).thenReturn(userToDeactivate);

        userDeactivationService.deactivateUser(1L);

        verify(eventService).deleteAllEvents(ownedEvents);

        assertTrue(userToDeactivate.getOwnedEvents().isEmpty());
    }

    @Test
    void testDeactivateUserWithRemoveUserFromParticipatedEvents() {
        when(userService.getUserById(1L)).thenReturn(userToDeactivate);
        when(userService.updateUser(userToDeactivate)).thenReturn(userToDeactivate);

        DeactivatedUserDto result = userDeactivationService.deactivateUser(userToDeactivate.getId());

        verify(eventService).updateAllEvents(anyList());

        assertTrue(result.idsParticipatedEvent().isEmpty());
        assertTrue(userToDeactivate.getParticipatedEvents().isEmpty());
        assertEquals(userToDeactivate.getId(), result.id());
        assertEquals(1L, (long) result.id());
    }

    @Test
    void testDeactivateUserWithStopMentorship() {
        when(userService.getUserById(1L)).thenReturn(userToDeactivate);
        doNothing().when(mentorshipService).stopMentorship(userToDeactivate);

        userDeactivationService.deactivateUser(userToDeactivate.getId());

        verify(mentorshipService).stopMentorship(userToDeactivate);
    }

    @Test
    void testDeactivateWithNonExistingUserWhenUserNotFound() {
        when(userService.getUserById(anyLong())).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> userDeactivationService.deactivateUser(anyLong()));
    }
}
