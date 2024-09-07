package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.repository.MentorshipRepository;
import school.faang.user_service.entity.User;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMentees_ReturnsEmptyList_WhenNoMenteesFound() {
        long userId = 1L;
        when(mentorshipRepository.findMenteesByMentorId(userId)).thenReturn(Collections.emptyList());
        List<MenteeDTO> result = mentorshipService.getMentees(userId);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testDeleteMentee_DeletesMentee_WhenMenteeExists() {
        long mentorId = 1L;
        long menteeId = 2L;
        User mentee = new User();
        when(mentorshipRepository.findMenteeByMentorIdAndMenteeId(mentorId, menteeId)).thenReturn(mentee);
        doNothing().when(mentorshipRepository).delete(mentee);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentee_DoesNothing_WhenMenteeDoesNotExist() {
        long mentorId = 1L;
        long menteeId = 2L;
        when(mentorshipRepository.findMenteeByMentorIdAndMenteeId(mentorId, menteeId)).thenReturn(null);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DeletesMentor_WhenMentorExists() {
        long menteeId = 1L;
        long mentorId = 2L;
        User mentor = new User();
        when(mentorshipRepository.findMentorByMenteeIdAndMentorId(menteeId, mentorId)).thenReturn(mentor);
        doNothing().when(mentorshipRepository).delete(mentor);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DoesNothing_WhenMentorDoesNotExist() {
        long menteeId = 1L;
        long mentorId = 2L;
        when(mentorshipRepository.findMentorByMenteeIdAndMentorId(menteeId, mentorId)).thenReturn(null);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}