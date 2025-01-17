package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class MentorshipServiceTest {
    @Mock
    private static MentorshipRepository mentorshipRepository;
    @Mock
    private static UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    private static User mentor;
    private static User mentee;
    private static Long mentorId;

    @BeforeAll
    static void setUp() {
        mentorId = 1L;
        mentor = new User();
        mentor.setId(mentorId);
        mentee = new User();
        mentee.setId(2L);
        mentee.setMentors(Collections.singletonList(mentor));
        mentee.setGoals(Collections.singletonList(new Goal()));
    }

    @Test
    void testStopUserMentorship_UserNotFound() {
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.empty());

        mentorshipService.stopUserMentorship(mentorId);

        verify(mentorshipRepository).findById(mentorId);
        verifyNoMoreInteractions(mentorshipRepository, userRepository);
    }

    @Test
    void testStopUserMentorship() {
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(userRepository.save(any(User.class))).thenReturn(mentee);

        mentorshipService.stopUserMentorship(mentorId);

        verify(mentorshipRepository).findById(mentorId);
        verify(userRepository, times(2)).save(any(User.class));
    }
}
