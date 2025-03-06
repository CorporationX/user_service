package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.impl.MentorshipServiceImpl;
import school.faang.user_service.service.impl.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @InjectMocks
    public MentorshipServiceImpl mentorshipService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    private User mentor;
    private User mentee;
    private User mentee2;

    @BeforeEach
    public void setUp() {
        mentor = new User();
        mentee = new User();
        mentee2 = new User();
        Goal goalWithMentee = new Goal();
        Goal goalWithoutMentee = new Goal();

        mentor.setId(1L);
        mentee.setId(2L);
        mentee2.setId(3L);
        goalWithMentee.setUsers(List.of(mentee));
        goalWithoutMentee.setUsers(new ArrayList<>());
        mentor.setSetGoals(List.of(goalWithMentee, goalWithoutMentee));
    }

    @Test
    void getMenteesWhenUserHasMentees() {
        long userId = 1L;
        User user = new User();
        User mentee1 = new User();
        User mentee2 = new User();
        UserDto dto1 = new UserDto();
        UserDto dto2 = new UserDto();
        List<User> mentees = List.of(mentee1, mentee2);

        user.setId(userId);
        mentee1.setId(2L);
        mentee2.setId(3L);
        user.setMentees(mentees);
        dto1.setId(2L);
        dto2.setId(3L);
        when(userService.findUserById(userId)).thenReturn(user);
        when(userMapper.toDto(mentees)).thenReturn(List.of(dto1, dto2));

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
        verify(userService, times(1)).findUserById(userId);
        verify(userMapper, times(1)).toDto(mentees);
    }

    @Test
    void getMenteesUserWithNoMenteesReturnsEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentees(Collections.emptyList());

        when(userService.findUserById(userId)).thenReturn(user);
        when(userMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findUserById(userId);
        verify(userMapper, times(1)).toDto(Collections.emptyList());
    }

    @Test
    void testGetMentorsWhenUserHasMentees() {
        long userId = 1L;
        User user = new User();
        User mentor1 = new User();
        User mentor2 = new User();
        List<User> mentors = List.of(mentor1, mentor2);
        UserDto mentor1Dto = new UserDto();
        UserDto mentor2Dto = new UserDto();

        user.setId(userId);
        mentor1.setId(2L);
        mentor2.setId(3L);
        user.setMentors(mentors);
        mentor1Dto.setId(2L);
        mentor2Dto.setId(3L);

        when(userService.findUserById(userId)).thenReturn(user);
        when(userMapper.toDto(mentors)).thenReturn(List.of(mentor1Dto, mentor2Dto));

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
        verify(userService, times(1)).findUserById(userId);
        verify(userMapper, times(1)).toDto(mentors);
    }

    @Test
    void testGetMentorsUserWithNoMenteesReturnsEmptyList() {
        long userId = 1L;
        User user = new User();

        user.setId(userId);
        user.setMentors(Collections.emptyList());
        when(userService.findUserById(userId)).thenReturn(user);
        when(userMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findUserById(userId);
        verify(userMapper, times(1)).toDto(Collections.emptyList());
    }

    @Test
    void testDeleteMenteeWhenMenteeExists() {
        User mentor = new User();
        User mentee = new User();

        mentor.setId(1L);
        mentee.setId(2L);
        mentor.setMentees(new ArrayList<>());
        mentor.getMentees().add(mentee);
        when(userService.findUserById(mentor.getId())).thenReturn(mentor);
        mentorshipService.deleteMentee( mentor.getId(),mentee.getId());
        verify(userService, times(1)).saveUser(mentor);
        assertTrue(mentor.getMentees().isEmpty());
    }

    @Test
    void testDeleteMenteeWhenMenteeDoesNotExist() {
        User mentor = new User();

        mentor.setId(1L);
        mentor.setMentees(new ArrayList<>());
        when(userService.findUserById(mentor.getId())).thenReturn(mentor);
        mentorshipService.deleteMentee( mentor.getId(), mentee2.getId());
        verify(userService, never()).saveUser(mentor);
    }

    @Test
    void testDeleteMentorWhenMenteeExists() {
        User mentee = new User();
        User mentor = new User();

        mentee.setId(1L);
        mentor.setId(2L);
        mentee.setMentors(new ArrayList<>());
        mentee.getMentors().add(mentor);
        when(userService.findUserById(mentee.getId())).thenReturn(mentee);
        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());
        verify(userService, times(1)).saveUser(mentee);
        assertTrue(mentee.getMentors().isEmpty());
    }

    @Test
    void testDeleteMentorWhenMenteeDoesNotExist() {
        User mentee = new User();

        mentee.setId(1L);
        mentee.setMentors(new ArrayList<>());
        when(userService.findUserById(mentee.getId())).thenReturn(mentee);
        mentorshipService.deleteMentor(mentee.getId(), 3L);
        verify(userService, never()).saveUser(mentee);
    }
}


