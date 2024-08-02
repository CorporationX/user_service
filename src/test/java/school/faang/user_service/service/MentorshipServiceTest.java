package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;
    @Captor
    private ArgumentCaptor<User> captor;
    private UserDto userDto;
    private User mentor;
    private User mentee;

    @BeforeEach
    void setUp() {

        mentor = new User();
        mentor.setId(1L);

        mentee = new User();
        mentee.setId(2L);

        userDto = new UserDto();
        userDto.setId(3L);
        userDto.setUserName("Bob");

        mentor.setMentees(List.of(mentee));
        mentee.setMentors(List.of(mentor));

    }

    @Test
    @DisplayName("Method - getMentees in case the mentor does not exist")
    void testIfMentorNotExists() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(mentor.getId()));
    }

    @Test
    @DisplayName("Method - getMentees in case the mentee does not exist")
    void testIfMenteeNotExists() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(mentee.getId()));
    }

    @Test
    @DisplayName("Method - getMentees in case the mentor exists and has mentees")
    void testGetMenteesIfMentorExists() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        List<UserDto> mentees = mentorshipService.getMentees(mentor.getId());
        assertEquals(1, mentees.size());
        verify(userMapper, times(1)).userToUserDto(mentee);

    }

    @Test
    @DisplayName("Method - getMentees in case the mentor exists and does not have any mentee")
    void testGetMenteesIfNoMenteesFound() {
        mentor.setMentees(Collections.emptyList());

        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        List<UserDto> mentees = mentorshipService.getMentees(mentor.getId());
        assertEquals(0, mentees.size());

    }
    @Test
    @DisplayName("Method - getMentors in case the mentee exists and has mentors")
    void testGetMentorsIfMenteeExists() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        List<UserDto> mentors = mentorshipService.getMentors(mentee.getId());
        assertEquals(1, mentors.size());
        verify(userMapper, times(1)).userToUserDto(mentor);
    }

    @Test
    @DisplayName("Method - getMentors in case the mentee exists and does not have any mentor")
    void testGetMentorsIfNoMentorsFound() {
        mentee.setMentors(Collections.emptyList());

        when(userRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        List<UserDto> mentors = mentorshipService.getMentors(mentee.getId());
        assertEquals(0, mentors.size());

    }

    @Test
    @DisplayName("Method - deleteMentee by Success")
    void testDeleteMenteeBySuccess() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());
        verify(userRepository, times(1)).delete(mentee);
    }

    @Test
    void testDeleteMentorBySuccess() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());
        verify(userRepository, times(1)).delete(mentor);

    }
    @Test
    @DisplayName("Method - deleteMentor if Mentor not found")
    void testDeleteMentorIfMentorNotFound() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));
        assertThrows(EntityNotFoundException.class, () -> {
            mentorshipService.deleteMentor(mentee.getId(),999L);
        });
        verify(userRepository, never()).delete(any());

    }

    @Test
    @DisplayName("Method - deleteMentee if Mentee not found")
    void testDeleteMenteeIfMenteeNotFound() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        assertThrows(EntityNotFoundException.class, () -> {
            mentorshipService.deleteMentee(999L, mentor.getId());
        });

        verify(userRepository, never()).delete(any());
    }


}