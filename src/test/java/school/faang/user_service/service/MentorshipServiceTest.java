package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    User user;
    User user2;


    @BeforeEach
    public void setUp() {
        mentorshipService = new MentorshipService(userRepository, userMapper);
        user = new User();
        user.setId(1L);
        user.setUsername("Name");
        user.setEmail("Email");
        user.setMentees(new ArrayList<>());
        user.setMentors(new ArrayList<>());
        user2 = new User();
        user2.setId(2L);
        user2.setUsername("Name2");
        user2.setEmail("Email2");
        user2.setMentees(new ArrayList<>());
        user2.setMentors(new ArrayList<>());
        user.getMentees().add(user2);
        user.getMentors().add(user2);
        user2.getMentors().add(user);
        user2.getMentees().add(user);
    }

    //getMentees tests
    @Test
    public void testGetMenteesUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () ->
                mentorshipService.getMentees(1L)
        );
        assertEquals("User not found.", userNotFoundException.getMessage());
    }

    @Test
    public void testGetMentees() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<User> mentees;
        mentees = user.getMentees();
        List<UserDto> menteesDto = mentees.stream().map(userMapper::toDto).toList();
        List<UserDto> result = mentorshipService.getMentees(1L);

        assertNotNull(result);
        assertEquals(menteesDto, result);
    }

    //getMentors test
    @Test
    public void testGetMentorsUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () ->
                mentorshipService.getMentees(1L)
        );
        assertEquals("User not found.", userNotFoundException.getMessage());
    }

    @Test
    public void testGetMentors() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<User> mentors;
        mentors = user.getMentors();
        List<UserDto> mentorsDto = mentors.stream().map(userMapper::toDto).toList();
        List<UserDto> result = mentorshipService.getMentors(1L);

        assertNotNull(result);
        assertEquals(mentorsDto, result);
    }

    //deleteMentee tests
    @Test
    public void testDeleteMenteeUserNotFound() {
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () ->
                mentorshipService.deleteMentee(1L, 3L)
        );
        assertEquals("User not found.", userNotFoundException.getMessage());
    }

    @Test
    public void testDeleteMenteeMenteeNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(user.getMentees().stream()
                        .filter(mentee -> mentee.equals(user2)).findFirst());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () ->
                mentorshipService.deleteMentee(3L, 1L)
        );

        assertEquals("Mentee not found.", userNotFoundException.getMessage());
    }

    @Test
    public void testDeleteMentee() {
        User userMock = mock(User.class);
        when(userMock.getMentees()).thenReturn(user.getMentees());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        mentorshipService.deleteMentee(2L, 1L);
        userMock.getMentees().remove(userMock);
//        verify(userMock, atLeastOnce()).getMentees().remove(user2);
    }

    //deleteMentor tests
    @Test
    public void testDeleteMentorUserNotFound() {
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () ->
                mentorshipService.deleteMentor(3L, 1L)
        );
        assertEquals("User not found.", userNotFoundException.getMessage());
    }

    @Test
    public void testDeleteMenteeMentorNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(user.getMentors().stream()
                        .filter(mentor -> mentor.equals(user2)).findFirst());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () ->
                mentorshipService.deleteMentor(1L, 3L)
        );
        assertEquals("Mentor not found.", userNotFoundException.getMessage());
    }
}
