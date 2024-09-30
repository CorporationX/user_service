package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MentorshipServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Test
    void testStopMentorship() {
        User mentor = new User();
        mentor.setId(1L);
        User mentee = new User();
        mentee.setId(2L);

        List<User> mentorMentees = new ArrayList<>();
        mentorMentees.add(mentee);

        List<User> menteeMentors = new ArrayList<>();
        menteeMentors.add(mentor);

        mentor.setMentees(mentorMentees);
        mentee.setMentors(menteeMentors);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentee));

        mentorshipService.stopMentorship(1L, 2L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).save(mentee);
        verify(userRepository, times(1)).save(mentor);
    }
}