package school.faang.user_service.service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.controller.mentorship.MentorshipService;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
public class MentorshipServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees_whenUserExists_returnsMentees() {
        Long mentorId = 1L;
        User mentor = new User();
        mentor.setId(mentorId);
        List<User> mentees;
        mentees = Collections.singletonList(new User());
        mentor.setMentees(mentees);
        Mockito.when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        List<User> actualMentees = mentorshipService.getMentees(mentorId);
        assertEquals(mentees, actualMentees);
    }

    @Test
    public void testGetMentees_whenUserDoesNotExist_returnsEmptyList() {
        long mentorId = 1L;
        Mockito.when(userRepository.findById(mentorId)).thenReturn(Optional.empty());
        List<User> actualMentees = mentorshipService.getMentees(mentorId);
        assertTrue(actualMentees.isEmpty());
    }
}