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
    @InjectMocks
    private MentorshipService mentorshipService;

    @BeforeEach
    void setUp() {
        Mockito.when(mentorshipRepository.findMenteesByMentorId(3L))
                .thenReturn(List.of(new User(), new User()));
    }

    @Test
    void getMentees_shouldMatchMenteesSize() {
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(3L);
        assertEquals(2, mentees.size());
    }

    @Test
    void getMentees_shouldInvokeFindMenteesByMentorIdMethod() {
        mentorshipService.getMentees(3L);
        Mockito.verify(mentorshipRepository).findMenteesByMentorId(3L);
    }

    @Test
    void getMentees_shouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> mentorshipService.getMentees(0L),
                "Invalid mentor id");
    }
}