package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMentees_ReturnsEmptyList_WhenNoMenteesFound() {
        long userId = 1L;
        when(mentorshipService.getMentees(userId)).thenReturn(Collections.emptyList());
        List<MenteeDTO> result = mentorshipController.getMentees(userId);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testDeleteMentee_DoesNothing_WhenMenteeDoesNotExist() {
        long mentorId = 1L;
        long menteeId = 2L;
        doNothing().when(mentorshipService).deleteMentee(menteeId, mentorId);
        mentorshipController.deleteMentee(mentorId, menteeId);
    }


    @Test
    public void testDeleteMentee_DeletesMentee_WhenMenteeExists() {
        long mentorId = 1L;
        long menteeId = 2L;
        doNothing().when(mentorshipService).deleteMentee(menteeId, mentorId);
        mentorshipController.deleteMentee(mentorId, menteeId);
    }

    @Test
    public void testDeleteMentor_DoesNothing_WhenMentorDoesNotExist() {
        long menteeId = 1L;
        long mentorId = 2L;
        doNothing().when(mentorshipService).deleteMentor(menteeId, mentorId);
        mentorshipController.deleteMentor(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DeletesMentor_WhenMentorExists() {
        long menteeId = 1L;
        long mentorId = 2L;
        doNothing().when(mentorshipService).deleteMentor(menteeId, mentorId);
        mentorshipController.deleteMentor(menteeId, mentorId);
    }
}
//done