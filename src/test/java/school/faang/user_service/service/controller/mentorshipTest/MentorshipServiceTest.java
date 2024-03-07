package school.faang.user_service.service.controller.mentorshipTest;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
public class MentorshipServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees1() {
        User mentees = User.builder()
                .id(1L)
                .mentees(Collections.singletonList(new User()))
                .build();
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(mentees));
        List<User> actualMentees = mentorshipService.getMentees(1L);
        assertEquals(mentees.getMentees(), actualMentees);
    }

    @Test
    public void testGetMentees2() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        List<User> actualMentees = mentorshipService.getMentees(userId);
        assertTrue(actualMentees.isEmpty());
    }

    @Test
    public void testGetMentors1() {
        User mentor = User.builder()
                .id(1L)
                .mentors(Collections.singletonList(new User()))
                .build();
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(mentor));
        List<User> actualMentors = mentorshipService.getMentors(1L);
        assertEquals(mentor.getMentors(), actualMentors);
    }

    @Test
    public void testGetMentors2() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        List<User> actualMentors = mentorshipService.getMentors(userId);
        assertTrue(actualMentors.isEmpty());
    }
    @Test
    public void testDeleteMentee() {
        User mentor = User.builder()
                .id(1L)
                .mentees(Collections.singletonList(new User()))
                .build();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        mentorshipService.deleteMentee(2L, 1L);
    }
    @Test
    public void testDeleteMentor() {
        User mentee = User.builder()
                .id(1L)
                .mentors(Collections.singletonList(new User()))
                .build();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentor(2L, 1L);
    }
}