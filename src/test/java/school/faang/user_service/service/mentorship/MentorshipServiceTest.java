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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MenteeMapper menteeMapper;
    @Mock
    private MentorMapper mentorMapper;
    @InjectMocks
    private MentorshipService mentorshipService;
    private static final Long CORRECT_USER_ID = 3L;
    private static final Long INCORRECT_USER_ID = 0L;

    @BeforeEach
    void setUp() {
        Mockito.when(mentorshipRepository.existsById(CORRECT_USER_ID))
                .thenReturn(true);
        Mockito.when(mentorshipRepository.findMenteesByMentorId(CORRECT_USER_ID))
                .thenReturn(List.of(new User(), new User()));
        Mockito.when(mentorshipRepository.findMentorsByUserId(CORRECT_USER_ID))
                .thenReturn(List.of(new User()));
    }

    @Test
    void getMentees_shouldMatchMenteesSize() {
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(CORRECT_USER_ID);
        assertEquals(2, mentees.size());
    }

    @Test
    void getMentees_shouldInvokeFindMenteesByMentorIdMethod() {
        mentorshipService.getMentees(CORRECT_USER_ID);
        Mockito.verify(mentorshipRepository).findMenteesByMentorId(CORRECT_USER_ID);
    }

    @Test
    void getMentees_shouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> mentorshipService.getMentees(INCORRECT_USER_ID),
                "Invalid mentor id");
    }

    @Test
    void getMentors_shouldMatchMentorsSize() {
        List<User> mentors = mentorshipRepository.findMentorsByUserId(CORRECT_USER_ID);
        assertEquals(1, mentors.size());
    }

    @Test
    void getMentors_shouldInvokeFindMentorsByUserIdMethod() {
        mentorshipService.getMentors(CORRECT_USER_ID);
        Mockito.verify(mentorshipRepository).findMentorsByUserId(CORRECT_USER_ID);
    }

    @Test
    void getMentors_shouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> mentorshipService.getMentors(INCORRECT_USER_ID),
                "Invalid user id");
    }
}