package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.goal.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipServiceImp;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private MentorshipServiceImp mentorshipService;
    private final Long id = 1L;

    @Test
    void stopMentorship_WithValidId() {
        doNothing().when(mentorshipRepository).deleteByMentorId(id);
        doNothing().when(goalRepository).updateMentorIdByMentorId(eq(id), isNull());

        mentorshipService.stopMentorship(id);

        verify(mentorshipRepository).deleteByMentorId(id);
        verify(goalRepository).updateMentorIdByMentorId(eq(id), isNull());
    }

    @Test
    void stopMentorship_WithNull() {
        assertThrows(NullPointerException.class,
                () -> mentorshipService.stopMentorship(null));

        verify(mentorshipRepository, never()).deleteByMentorId(id);
        verify(goalRepository, never()).updateMentorIdByMentorId(eq(id), isNull());
    }
}