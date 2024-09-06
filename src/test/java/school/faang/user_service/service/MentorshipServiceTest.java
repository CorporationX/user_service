package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.goal.mentorship.MentorshipRepository;

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

    @Test
    void stopMentorship_WithValidId() {
        Long id = 1L;
        doNothing().when(mentorshipRepository).deleteByMentorId(id);
        doNothing().when(goalRepository).updateMentorIdByMentorId(eq(id), isNull());

        mentorshipService.stopMentorship(id);

        verify(mentorshipRepository).deleteByMentorId(id);
        verify(goalRepository).updateMentorIdByMentorId(eq(id), isNull());
    }

    @Test
    void stopMentorship_WithNull() {
        Long id = null;

        assertThrows(NullPointerException.class,
                () -> mentorshipService.stopMentorship(id));

        verify(mentorshipRepository, never()).deleteByMentorId(id);
        verify(goalRepository, never()).updateMentorIdByMentorId(eq(id), isNull());
    }
}