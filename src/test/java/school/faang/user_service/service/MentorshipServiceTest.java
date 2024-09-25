package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.mentee.MenteeDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteeMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MenteeMapper menteeMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMentees_ReturnsEmptyList_WhenNoMenteesFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        List<MenteeDTO> result = mentorshipService.getMentees(userId);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testDeleteMentee_DeletesMentee_WhenMenteeExists() {
        long mentorId = 1L;
        long menteeId = 2L;
        User mentor = new User();
        User mentee = new User();
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        doNothing().when(userRepository).delete(mentee);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentee_DoesNothing_WhenMenteeDoesNotExist() {
        long mentorId = 1L;
        long menteeId = 2L;
        when(userRepository.findById(mentorId)).thenReturn(Optional.empty());
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DeletesMentor_WhenMentorExists() {
        long menteeId = 1L;
        long mentorId = 2L;
        User mentee = new User();
        User mentor = new User();
        when(userRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        doNothing().when(userRepository).delete(mentor);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DoesNothing_WhenMentorDoesNotExist() {
        long menteeId = 1L;
        long mentorId = 2L;
        when(userRepository.findById(menteeId)).thenReturn(Optional.empty());
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}