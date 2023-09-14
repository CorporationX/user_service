package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserRepository userRepository;
    @Spy
    @InjectMocks
    private UserService userService;
    @Mock
    private GoalService goalService;
    @Mock
    private EventService eventService;

    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1L)
                .username("test")
                .email("abcd@a.com")
                .phone("8-800-555-35-35")
                .password("Qdsf32jsdfad")
                .active(true)
                .aboutMe("I love Java")
                .country(new Country())
                .city("Great Ustuk")
                .experience(5)
                .build();
    }

    @Test
    void areOwnedSkills() {
        assertTrue(userService.areOwnedSkills(1L, List.of()));
    }

    @Test
    void areOwnedSkillsFalse() {
        Mockito.when(userRepository.countOwnedSkills(1L, List.of(2L))).thenReturn(3);
        assertFalse(userService.areOwnedSkills(1L, List.of(2L)));
    }

    @Test
    void getUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        userService.getUser(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getUsersByIds() {
        Mockito.when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()));
        List<UserDto> users = userService.getUsersByIds(List.of(1L, 2L));
        assertEquals(2, users.size());
    }

    @Test
    void deactivateUserValidTest() {
        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();

        Goal deactivateUserInGoal1 = Goal.builder()
                .id(1L)
                .status(GoalStatus.ACTIVE)
                .users(new ArrayList<>(Arrays.asList(user, user2, user3)))
                .build();
        Goal deactivateUserInGoal2 = Goal.builder()
                .id(2L)
                .status(GoalStatus.ACTIVE)
                .users(List.of(user))
                .build();
        Goal deactivateUserGoal = Goal.builder()
                .id(3L)
                .mentor(user)
                .build();

        Event deactivateUserEvent1 = Event.builder()
                .id(1L)
                .status(EventStatus.PLANNED)
                .owner(user)
                .build();
        Event deactivateUserEvent2 = Event.builder()
                .id(2L)
                .status(EventStatus.IN_PROGRESS)
                .owner(user)
                .build();
        Event deactivateUserInEvent = Event.builder()
                .id(3L)
                .status(EventStatus.PLANNED)
                .attendees(new ArrayList<>(Arrays.asList(user, user2)))
                .build();

        user.setGoals(new ArrayList<>(Arrays.asList(deactivateUserInGoal1, deactivateUserInGoal2)));
        user.setSetGoals(new ArrayList<>(Collections.singletonList(deactivateUserGoal)));
        user.setOwnedEvents(new ArrayList<>(Arrays.asList(deactivateUserEvent1, deactivateUserEvent2)));
        user.setParticipatedEvents(new ArrayList<>(Collections.singletonList(deactivateUserInEvent)));

        Mockito.doReturn(user).when(userService).getUserById(user.getId());

        userService.deactivateUser(user.getId());

        Mockito.verify(userService, Mockito.atLeast(1))
                .getUserById(Mockito.anyLong());

        Mockito.verify(goalService, Mockito.atLeast(1))
                .setGoalStatusOnHold(Mockito.anyLong());
        Mockito.verify(goalService, Mockito.times(1))
                .setGoalStatusOnHold(deactivateUserInGoal2.getId());

        Mockito.verify(eventService, Mockito.times(2))
                .cancelEvent(Mockito.anyLong());
        Mockito.verify(eventService, Mockito.times(1))
                .cancelEvent(deactivateUserEvent1.getId());
        assertEquals(user.getSetGoals().size(), 0);
    }

    @Test
    void deactivateUserAlreadyDeactivatedUserTest() {
        user.setActive(false);
        Mockito.doReturn(user).when(userService).getUserById(user.getId());

        assertThrows(DataValidationException.class, () -> userService.deactivateUser(user.getId()));
    }
}