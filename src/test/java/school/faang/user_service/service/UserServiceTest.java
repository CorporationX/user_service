package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
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
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GoalService goalService;

    @Mock
    private MentorshipService mentorshipService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private User userToDeactivateMock;

    @Mock
    private Event eventOwnedMock1, eventOwnedMock2;

    @Mock
    private Goal goalOwnerMock1, goalOwnerMock2, goalSettingMock1, goalSettingMock2;

    @Captor
    private ArgumentCaptor<User> captorUser;

    @InjectMocks
    private UserService userService;

    private User user;
    private Event eventOwned1;
    private Event eventOwned2;
    private List<Goal> ownerGoals;
    private List<Goal> settingGoals;
    private User userToDeactivate;
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

        Goal goalOwner2 = new Goal();
        goalOwner2.setTitle("Goal 2");

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

        List<User> attendees = new ArrayList<>();


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
    void testGetUserByIdWithExistingUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void testDeactivateWhenAllMethodsWork() {
        List<Event> participatedAttendeeEvents = new ArrayList<>();
        participatedAttendeeEvents.add(eventOwnedMock1);
        participatedAttendeeEvents.add(eventOwnedMock2);

        User attendee1 = User.builder()
                .participatedEvents(participatedAttendeeEvents)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userToDeactivateMock));
        when(userToDeactivateMock.getOwnedEvents()).thenReturn(List.of(eventOwnedMock1, eventOwnedMock2));
        when(eventOwnedMock1.getAttendees()).thenReturn(List.of(attendee1));
        when(eventOwnedMock2.getAttendees()).thenReturn(List.of(attendee1));
        when(eventOwnedMock1.getId()).thenReturn(1L);
        when(eventOwnedMock2.getId()).thenReturn(2L);
        when(userToDeactivateMock.getGoals()).thenReturn(List.of(goalOwnerMock1, goalOwnerMock2));
        when(userToDeactivateMock.getSettingGoals()).thenReturn(List.of(goalSettingMock1, goalSettingMock2));

        userService.deactivateUser(1L);

        verify(goalService).removeGoalsWithoutExecutingUsers(List.of(goalOwnerMock1, goalOwnerMock2));
        verify(goalService).removeGoalsWithoutExecutingUsers(List.of(goalSettingMock1, goalSettingMock2));
        verify(mentorshipService).stopMentorship(userToDeactivateMock);
        verify(eventRepository, times(1)).deleteById(eventOwnedMock1.getId());
        verify(eventRepository, times(1)).deleteById(eventOwnedMock2.getId());
        verify(userRepository, times(2)).save(attendee1);

        assertFalse(userToDeactivateMock.isActive(), "User should be deactivated");
    }

    @Test
    void testDeactivateUserWhenSavesCorrectUser() {
        when(userRepository.findById(userToDeactivate.getId()))
                .thenReturn(Optional.ofNullable(userToDeactivate));
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(ownerGoals);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(settingGoals);

        userService.deactivateUser(userToDeactivate.getId());

        verify(userRepository, times(2)).save(captorUser.capture());
        User deactevatedUser = captorUser.getValue();
        assertFalse(userToDeactivate.isActive());
        assertFalse(deactevatedUser.isActive());
        assertEquals(userToDeactivate, deactevatedUser);
    }

    @Test
    void testDeactivateUserWithExistingUserWhenAllDelCompleteWithConsistentData() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToDeactivate));
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(ownerGoals);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(settingGoals);
        when(userRepository.save(userToDeactivate)).thenReturn(userToDeactivate);
        when(userRepository.save(attendee1)).thenReturn(attendee1);

        DeactivatedUserDto result = userService.deactivateUser(userToDeactivate.getId());

        verify(mentorshipService).stopMentorship(userToDeactivate);
        verify(goalService, times(2)).removeGoalsWithoutExecutingUsers(anyList());
        verify(eventRepository, times(1)).deleteById(eventOwned1.getId());
        verify(eventRepository, times(1)).deleteById(eventOwned2.getId());
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
        when(userRepository.findById(userToDeactivate.getId()))
                .thenReturn(Optional.ofNullable(userToDeactivate));
        when(userRepository.save(userToDeactivate)).thenReturn(userToDeactivate);
        when(userRepository.save(attendee1)).thenReturn(attendee1);

        DeactivatedUserDto result = userService.deactivateUser(userToDeactivate.getId());

        verify(userRepository, times(1)).save(userToDeactivate);

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

        when(userRepository.findById(1L)).thenReturn(Optional.of(userToDeactivate));
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(ownedGoals);
        doNothing().when(goalService).removeGoalsWithoutExecutingUsers(settingGoals);

        userService.deactivateUser(userToDeactivate.getId());

        verify(goalService, times(2)).removeGoalsWithoutExecutingUsers(anyList());
        assertTrue(userToDeactivate.getGoals().isEmpty());
        assertTrue(userToDeactivate.getSettingGoals().isEmpty());
    }

    @Test
    void testDeactivateUserWithStopScheduledEvents() {
        List<Event> ownedEvents = userToDeactivate.getOwnedEvents();
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToDeactivate));

        userService.deactivateUser(userToDeactivate.getId());

        for (Event event : ownedEvents) {
            verify(userRepository).save(event.getAttendees().get(0));
            verify(eventRepository).deleteById(event.getId());
        }

        assertTrue(userToDeactivate.getOwnedEvents().isEmpty());
    }

    @Test
    void testDeactivateUserWithRemoveUserFromParticipatedEvents() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToDeactivate));
        when(userRepository.save(userToDeactivate)).thenReturn(userToDeactivate);
        when(userRepository.save(attendee1)).thenReturn(attendee1);
        when(eventRepository.save(any(Event.class))).thenReturn(mock(Event.class));

        DeactivatedUserDto result = userService.deactivateUser(userToDeactivate.getId());

        verify(eventRepository).save(any(Event.class));

        assertTrue(result.idsParticipatedEvent().isEmpty());
        assertTrue(userToDeactivate.getParticipatedEvents().isEmpty());
        assertEquals(userToDeactivate.getId(), result.id());
        assertEquals(1, result.id());
    }

    @Test
    void testDeactivateUserWithStopMentorship() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToDeactivate));
        doNothing().when(mentorshipService).stopMentorship(userToDeactivate);

        userService.deactivateUser(userToDeactivate.getId());

        verify(mentorshipService).stopMentorship(userToDeactivate);
    }

    @Test
    void testDeactivateWithNonExistingUserWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> userService.deactivateUser(anyLong()));
    }
}