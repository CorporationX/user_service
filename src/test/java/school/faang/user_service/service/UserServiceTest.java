package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private GoalService goalService;
    @InjectMocks
    private UserService userService;

    @Test
    public void deactivateUserById() {
        User u = new User();
        u.setId(1L);
        u.setUsername("user");
        u.setActive(true);

        Goal g = new Goal();
        g.setId(1L);
        g.setStatus(GoalStatus.ACTIVE);
        g.setUsers(new ArrayList<>(List.of(u)));
        g.setMentor(u);

        User ment = new User();
        ment.setId(2L);
        ment.setMentors(new ArrayList<>(List.of(u)));

        Event ev = new Event();
        ev.setId(1L);
        ev.setOwner(u);
        ev.setStatus(EventStatus.PLANNED);

        u.setGoals(new ArrayList<>(List.of(g)));
        u.setMentees(new ArrayList<>(List.of(ment)));
        u.setOwnedEvents(new ArrayList<>(List.of(ev)));


        UserDto excepted = new UserDto();
        excepted.setId(1L);
        excepted.setUsername("user");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userRepository.save(u)).thenReturn(u);
        when(userMapper.toDto(u)).thenReturn(excepted);


        UserDto userDto = userService.deactivationUserById(u.getId());

        verify(userRepository,times(1)).findById(1L);
        verify(goalService, times(1)).deleteGoal(g.getId());
        verify(eventRepository, times(1)).deleteById(ev.getId());
        verify(mentorshipService, times(1)).deleteMentorForHisMentees(u.getId(), List.of(ment));
        verify(userRepository, times(1)).save(u);
        verify(userMapper, times(1)).toDto(u);
        assertFalse(u.isActive());
        assertEquals(excepted,userDto);
    }
}