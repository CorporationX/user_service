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
    private List<User> mentees;
    private List<UserDto> users2;
    private List<UserDto> usersDto;
    private Long id;
    private Long id2;
    private Long id3;

    @BeforeEach
    void setUp() {
        id = 1L;
        id2 = 2L;
        id3 = 3L;
        userDto = UserDto.builder().id(id).build();
        usersDto = new ArrayList<>();
        users = new ArrayList<>();
        user1 = User.builder().id(id).mentors(users).mentees(users).build();
        users2 = List.of(userDto);
        mentees = new ArrayList<>();
        mentees.add(user1);
        user2 = User.builder().id(id2).mentees(mentees).build();
        user3 = User.builder().id(id3).mentors(mentees).build();

    }

    @Test
    void testGetMentees_ShouldThrowsException() {
        whenGetUserByIdThenThrowException();
        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(id));
    }

    @Test
    void testGetMentees_ShouldReturnsListOfMenteesDto() {
        whenGetUserByIdThenReturnUser(id2, user2);
        assertEquals(users2, mentorshipService.getMentees(id2));
    }

    @Test
    void testGetMentees_ShouldReturnsEmptyList() {
        whenGetUserByIdThenReturnUser(id, user1);
        assertEquals(usersDto, mentorshipService.getMentees(id));
    }

    @Test
    void testGetMentors_ShouldThrowsException() {
        whenGetUserByIdThenThrowException();
        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(id));
    }

    @Test
    void testGetMentors_ShouldReturnsListOfMentorsDto() {
        whenGetUserByIdThenReturnUser(id3, user3);
        assertEquals(users2, mentorshipService.getMentors(id3));
    }

    @Test
    void testGetMentors_ShouldReturnsEmptyList() {
        whenGetUserByIdThenReturnUser(id, user1);
        assertEquals(usersDto, mentorshipService.getMentors(id));
    }

    @Test
    void testRemoveMenteeOfMentor() {
        whenGetUserByIdThenReturnUser(id2, user2);
        whenGetUserByIdThenReturnUser(id, user1);

        mentorshipService.removeMenteeOfMentor(id2, id);

        verify(userService, times(1)).getUserById(id2);
        verify(userService, times(1)).getUserById(id);
        assertEquals(user2.getMentees(),user1.getMentors());
    }

    @Test
    void testRemoveMentorOfMentee() {
        whenGetUserByIdThenReturnUser(id2, user2);
        whenGetUserByIdThenReturnUser(id, user1);

        mentorshipService.removeMentorOfMentee(id2, id);

        verify(userService, times(1)).getUserById(id2);
        verify(userService, times(1)).getUserById(id);
        assertEquals(user1.getMentors(),user1.getMentees());
    }

    @Test
    void testGetMentors_ShouldCallsRepositoryMethod() {
        whenGetUserByIdThenReturnUser(id, user1);
        mentorshipService.getMentors(user1.getId());
        verify(userService, times(1)).getUserById(user1.getId());
    }

    private void whenGetUserByIdThenThrowException() {
        when(userService.getUserById(id)).thenThrow(DataValidationException.class);
    }

    private void whenGetUserByIdThenReturnUser(Long id, User user1) {
        when(userService.getUserById(id)).thenReturn(user1);
    }
}