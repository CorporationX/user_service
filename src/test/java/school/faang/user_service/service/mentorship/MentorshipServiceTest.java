package school.faang.user_service.service.mentorship;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.UserValidator;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MenteeMapper menteeMapper;
    @Mock
    private MentorMapper mentorMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private MentorshipValidator mentorshipValidator;
    @InjectMocks
    private MentorshipService mentorshipService;
    private static final long MENTOR_ID = 1L;
    private static final long MENTEE_ID = 3L;

    @BeforeEach
    void setUp() {
        Mockito.when(mentorshipRepository.findMenteesByMentorId(MENTOR_ID))
                .thenReturn(List.of(new User(), new User()));
        Mockito.when(mentorshipRepository.findMentorsByUserId(MENTEE_ID))
                .thenReturn(List.of(new User(), new User(), new User()));
    }

    @Test
    void getMentees_shouldMatchMenteesSize() {
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(MENTOR_ID);
        assertEquals(2, mentees.size());
    }

    @Test
    void getMentees_shouldInvokeValidateUserMethod() {
        mentorshipService.getMentees(MENTOR_ID);
        Mockito.verify(userValidator).validateUser(MENTOR_ID);
    }

    @Test
    void getMentees_shouldInvokeFindMenteesByMentorIdMethod() {
        mentorshipService.getMentees(MENTOR_ID);
        Mockito.verify(mentorshipRepository).findMenteesByMentorId(MENTOR_ID);
    }

    @Test
    void getMentors_shouldMatchMentorsSize() {
        List<User> mentors = mentorshipRepository.findMentorsByUserId(MENTEE_ID);
        assertEquals(3, mentors.size());
    }

    @Test
    void getMentors_shouldInvokeValidateUserMethod() {
        mentorshipService.getMentors(MENTEE_ID);
        Mockito.verify(userValidator).validateUser(MENTEE_ID);
    }

    @Test
    void getMentors_shouldInvokeFindMentorsByUserIdMethod() {
        mentorshipService.getMentors(MENTEE_ID);
        Mockito.verify(mentorshipRepository).findMentorsByUserId(MENTEE_ID);
    }

    @Test
    void deleteMentee_shouldInvokeValidateToDeleteMenteeMethod() {
        mentorshipService.deleteMentee(MENTOR_ID, MENTEE_ID);
        Mockito.verify(mentorshipValidator).validateToDeleteMentee(MENTOR_ID, MENTEE_ID);
    }

    @Test
    void deleteMentee_shouldInvokeDeleteMenteeMethod() {
        mentorshipService.deleteMentee(MENTOR_ID, MENTEE_ID);
        Mockito.verify(mentorshipRepository).deleteMentee(MENTOR_ID, MENTEE_ID);
    }
}