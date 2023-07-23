package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MentorshipValidator mentorshipValidator;
    private User mentor = new User();
    private User mentee = new User();

    private long mentorId;
    private long menteeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mentorId = 1L;
        menteeId = 2L;

        mentor.setId(mentorId);
        mentee.setId(menteeId);

        List<User> mentees = new ArrayList<>();
        mentees.add(mentee);
        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);

        mentor.setMentees(mentees);
        mentee.setMentors(mentors);

        Mockito.when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(mentor));
        Mockito.when(mentorshipValidator.findUserByIdValidate(Optional.of(mentee)))
                .thenReturn(mentee);
        Mockito.when(mentorshipValidator.findUserByIdValidate(Optional.of(mentor)))
                .thenReturn(mentor);
    }

    @Test
    void deleteMentee() {
        boolean requestFirst = mentorshipService.deleteMentee(menteeId, mentorId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        Mockito.verify(mentorshipValidator, Mockito.times(1)).findUserByIdValidate(Optional.of(mentee));
        Mockito.verify(mentorshipValidator, Mockito.times(1)).findUserByIdValidate(Optional.of(mentor));

        assertTrue(requestFirst);

        boolean requestSecond = mentorshipService.deleteMentee(menteeId, mentorId);
        assertFalse(requestSecond);
    }

    @Test
    void deleteMentor() {
        boolean requestFirst = mentorshipService.deleteMentor(menteeId, mentorId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        Mockito.verify(mentorshipValidator, Mockito.times(1)).findUserByIdValidate(Optional.of(mentee));
        Mockito.verify(mentorshipValidator, Mockito.times(1)).findUserByIdValidate(Optional.of(mentor));

        assertTrue(requestFirst);

        boolean requestSecond = mentorshipService.deleteMentor(menteeId, mentorId);
        assertFalse(requestSecond);
    }
}