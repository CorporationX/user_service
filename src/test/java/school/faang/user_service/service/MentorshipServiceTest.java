package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private User user;
    @InjectMocks
    private MentorshipService mentorshipService;


    @Test
    public void getMentees_When() {
        RuntimeException runtimeException = assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentees(null));
        assertEquals("User with id " + null + " not found", runtimeException.getMessage());
    }

    @Test
    public void getMentees_CorrectAnswer() {
        long mentorId = 2;
        User user = new User();
        user.setId(mentorId);
        Optional<User> userOptional = Optional.of(user);
        when(mentorshipRepository.findById(anyLong())).thenReturn(userOptional);
        when(mentorshipService.getMentees(mentorId)).thenReturn(List.of(new UserDto(), new UserDto()));

        List<UserDto> userDto = mentorshipService.getMentees(mentorId);

        assertEquals(2, userDto.size());
    }

    @Test
    public void getMentors_When() {
        RuntimeException runtimeException = assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentors(null));
        assertEquals("User with id " + null + " not found", runtimeException.getMessage());
    }

    @Test
    public void getMentors_CorrectAnswer() {
        long menteeId = 2;
        User user = new User();
        Optional<User> userOptional = Optional.of(user);
        when(mentorshipRepository.findById(anyLong())).thenReturn(userOptional);
        when(mentorshipService.getMentors(menteeId)).thenReturn(List.of(new UserDto(), new UserDto()));

        List<UserDto> userDto = mentorshipService.getMentors(menteeId);

        assertEquals(2, userDto.size());
    }

    @Test
    public void deleteMentee_WhenMentorIsNull() {
        RuntimeException runtimeException = assertThrows(IllegalArgumentException.class, () -> mentorshipService.deleteMentee(1L, null));
        assertEquals("Mentor with id " + null + " not found", runtimeException.getMessage());
    }

    @Test
    public void deleteMentee_CorrectAnswer() {
        User mentee1 = new User();
        User mentee2 = new User();
        User mentor = new User();
        List<User> mentees = new ArrayList<>();
        mentees.add(mentee1);
        mentees.add(mentee2);
        mentor.setMentees(mentees);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mentor)).thenReturn(Optional.of(mentee1));
        mentorshipService.deleteMentee(mentee1.getId(), mentor.getId());
        List<User> userList = mentor.getMentees();
        assertEquals(1, userList.size());
    }

    @Test
    public void deleteMentor_CorrectAnswer() {
        User mentor1 = new User();
        User mentor2 = new User();
        User mentee = new User();
        List<User> mentors = new ArrayList<>();
        mentors.add(mentor1);
        mentors.add(mentor2);
        mentee.setMentors(mentors);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mentor1)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentor(mentee.getId(), mentor1.getId());
        List<User> userList = mentee.getMentors();
        assertEquals(1, userList.size());
    }
}