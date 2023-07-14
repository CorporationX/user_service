package school.faang.user_service.service.mentorship;

import java.util.List;
import java.util.Optional;
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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipServiceTest {
    private static final long MENTOR_ID = 1L;
    private static final long INCORRECT_MENTOR_ID = 0L;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MentorshipService mentorshipService;

    @BeforeEach
    void setUp() {
        when(userRepository.findById(INCORRECT_MENTOR_ID))
                .thenReturn(null);

        User mentor = new User();
        mentor.setMentees(List.of(new User(), new User()));
        when(userRepository.findById(MENTOR_ID))
                .thenReturn(Optional.of(mentor));
    }

    @Test
    void getMentees_shouldMatchMenteesSize() {
        List<User> mentees = mentorshipService.getMentees(MENTOR_ID);
        assertEquals(2, mentees.size());
    }

    @Test
    void getMentees_shouldInvokeFindByIdMethod() {
        mentorshipService.getMentees(MENTOR_ID);
        Mockito.verify(userRepository).findById(MENTOR_ID);
    }

    @Test
    void getMentees_shouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> mentorshipService.getMentees(INCORRECT_MENTOR_ID),
                "Invalid mentor id");
    }
}