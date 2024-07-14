package school.faang.user_service.service.mentorship;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.mentorship.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Spy
    private UserMapper mapper;

    @Test
    void getMenteesTest_correct() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        User mentee1 = new User();
        User mentee2 = new User();
        List<User> mentees = List.of(mentee1, mentee2);
        user.setMentees(mentees);
        Optional<User> optionalUser = Optional.of(user);

        when(mentorshipRepository.findById(userId)).thenReturn(optionalUser);

        List<UserDto> expected = mapper.toDtoList(mentees);
        List<UserDto> actual = mentorshipService.getMentees(1);

        assertEquals(expected, actual);
    }

    @Test
    void getMenteesTest_correctWithEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        Optional<User> optionalUser = Optional.of(user);

        when(mentorshipRepository.findById(userId)).thenReturn(optionalUser);

        List<UserDto> expected = new ArrayList<>();
        List<UserDto> actual = mentorshipService.getMentees(1);

        assertEquals(expected, actual);
    }

    @Test
    void getMenteesTest_UserNotFoundException() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> mentorshipService.getMentees(1));
        assertEquals(exception.getMessage(), "User not found");
    }

    @Test
    void getMentorsTest_correct() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        User mentor1 = new User();
        User mentor2 = new User();
        List<User> mentors = List.of(mentor1, mentor2);
        user.setMentees(mentors);
        Optional<User> optionalUser = Optional.of(user);

        when(mentorshipRepository.findById(userId)).thenReturn(optionalUser);

        List<UserDto> expected = mapper.toDtoList(mentors);
        List<UserDto> actual = mentorshipService.getMentees(1);

        assertEquals(expected, actual);
    }

    @Test
    void getMentorsTest_correctWithEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        Optional<User> optionalUser = Optional.of(user);

        when(mentorshipRepository.findById(userId)).thenReturn(optionalUser);

        List<UserDto> expected = new ArrayList<>();
        List<UserDto> actual = mentorshipService.getMentors(1);

        assertEquals(expected, actual);
    }

    @Test
    void getMentorsTest_UserNotFoundException() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> mentorshipService.getMentors(1));
        assertEquals(exception.getMessage(), "User not found");
    }

    @Test
    void deleteMenteeTest_correct() {
        User mentee = new User();
        mentee.setId(1);
        User mentor1 = new User();
        mentor1.setId(2);
        User mentor2 = new User();
        mentor2.setId(3);
        List<User> mentors = List.of(mentor1, mentor2);
        mentee.setMentors(mentors);

        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentor(1,2);

        assertEquals(mentee.getMentors().size(), mentors.size() - 1);
    }

    @Test
    void deleteMenteeTest_MentorNotFoundException() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> mentorshipService.deleteMentee(1, 2));
        assertEquals(exception.getMessage(), "Mentor not found");
    }

    @Test
    void deleteMenteeTest_MenteeNotFoundException() {
        User mentee = new User();
        mentee.setId(1);
        User mentor1 = new User();
        mentor1.setId(2);
        User mentor2 = new User();
        mentor2.setId(3);
        List<User> mentors = List.of(mentor1, mentor2);
        mentee.setMentors(mentors);

        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.of(mentee));

        Exception exception = assertThrows(NotFoundException.class, () -> mentorshipService.deleteMentee(1,4));
        assertEquals(exception.getMessage(), "Mentee not found");
    }

    @Test
    void deleteMentorTest_correct() {
        User mentor = new User();
        mentor.setId(1);
        User mentee1 = new User();
        mentee1.setId(2);
        User mentee2 = new User();
        mentee2.setId(3);
        List<User> mentees = List.of(mentee1, mentee2);
        mentor.setMentors(mentees);

        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.of(mentor));

        mentorshipService.deleteMentor(1,2);

        assertEquals(mentor.getMentors().size(), mentees.size() - 1);
    }

    @Test
    void deleteMentorTest_MenteeNotFoundException() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> mentorshipService.deleteMentor(1, 2));
        assertEquals(exception.getMessage(), "Mentee not found");
    }

    @Test
    void deleteMentorTest_MentorNotFoundException() {
        User mentor = new User();
        mentor.setId(1);
        User mentee1 = new User();
        mentee1.setId(2);
        User mentee2 = new User();
        mentee2.setId(3);
        List<User> mentees = List.of(mentee1, mentee2);
        mentor.setMentors(mentees);

        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.of(mentor));

        Exception exception = assertThrows(NotFoundException.class, () -> mentorshipService.deleteMentor(1,4));
        assertEquals(exception.getMessage(), "Mentor not found");
    }
}
