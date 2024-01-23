package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private UserService userService;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;
    private User user1;
    private User user2;
    private User user3;
    private UserDto userDto;
    private List<User> users;
    private List<User> users3;
    private List<UserDto> users2;
    private List<UserDto> usersDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder().id(1L).build();
        usersDto = new ArrayList<>();
        users = new ArrayList<>();
        user1 = User.builder().id(1L).mentors(users).mentees(users).build();
        users2 = List.of(userDto);
        users3 = List.of(user1);
        user2 = User.builder().id(2L).mentees(users3).build();
        user3 = User.builder().id(3L).mentors(users3).build();
    }

    @Test
    void testGetMentees_ShouldThrowsException() {
        when(userService.getUserById(1L)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(1L));
    }

    @Test
    void testGetMentees_ShouldReturnsListOfMenteesDto() {
        when(userService.getUserById(2L))
                .thenReturn(user2);
        assertEquals(users2, mentorshipService.getMentees(2L));
    }

    @Test
    void testGetMentees_ShouldReturnsEmptyList() {
        when(userService.getUserById(1L)).thenReturn(user1);
        assertEquals(usersDto, mentorshipService.getMentees(1L));
    }

    @Test
    void testGetMentors_ShouldThrowsException() {
        when(userService.getUserById(1L)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(1L));
    }

    @Test
    void testGetMentors_ShouldReturnsListOfMentorsDto() {
        when(userService.getUserById(3L))
                .thenReturn(user3);
        assertEquals(users2, mentorshipService.getMentors(3L));
    }

    @Test
    void testGetMentors_ShouldReturnsEmptyList() {
        when(userService.getUserById(1L))
                .thenReturn(user1);
        assertEquals(usersDto, mentorshipService.getMentors(1L));
    }

    @Test
    void testRemoveMentorsMentee_ShouldThrowsException() {
        when(userService.getUserById(1L)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class,
                () -> mentorshipService.removeMentorsMentee(1L, 2L));
    }

    @Test
    void testRemoveMentorOfMentee_ShouldThrowsException() {
        when(userService.getUserById(1L)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class,
                () -> mentorshipService.removeMentorOfMentee(1L, 2L));
    }

    @Test
    void testGetMentors_ShouldCallsRepositoryMethod() {
        when(userService.getUserById(1L)).thenReturn(user1);
        mentorshipService.getMentors(user1.getId());
        verify(userService, times(1)).getUserById(user1.getId());
    }
}