package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.MentorshipRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapperImpl userMapper;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    private final Long id = 1L;
    private User mentor;
    private User mentee;

    @BeforeEach
    void setUp() {
        mentor = new User();
        mentor.setId(10L);

        mentee = new User();
        mentee.setId(20L);

        mentor.setMentees(List.of(mentee));
        mentee.setMentors(List.of(mentor));
    }

    @Test
    void getMentees_WhenMentorDoesNotExists() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(mentor.getId()));
    }

    @Test
    void getMentors_WhenMenteeNotExists() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(mentee.getId()));
    }

    @Test
    void getMentees_WhenMentorExists() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        List<UserDto> mentees = mentorshipService.getMentees(mentor.getId());
        assertEquals(1, mentees.size());
        verify(userMapper, times(1)).toDto(mentee);
    }

    @Test
    void getMentors_WhenNoMentorsFound() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        mentee.setMentors(Collections.emptyList());

        List<UserDto> mentors = mentorshipService.getMentors(mentee.getId());
        assertEquals(0, mentors.size());
    }

    @Test
    void getMentors_WhenMenteeExists() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        List<UserDto> mentors = mentorshipService.getMentors(mentee.getId());
        assertEquals(1, mentors.size());

        verify(userMapper, times(1)).toDto(mentor);
    }

    @Test
    void getMentees_WhenNoMenteesFound() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        mentor.setMentees(Collections.emptyList());
        List<UserDto> mentees = mentorshipService.getMentees(mentor.getId());
        assertEquals(0, mentees.size());
    }

    @Test
    void deleteMentee_WhenSuccess() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());
        verify(userRepository, times(1)).delete(mentee);
    }

    @Test
    void deleteMentor_WhenSuccess() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());
        verify(userRepository, times(1)).delete(mentor);
    }

    @Test
    void deleteMentor_WhenMentorNotFound() {
        when(userRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(mentee.getId(), 999L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteMentee_WhenMenteeNotFound() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(999L, mentor.getId()));
        verify(userRepository, never()).delete(any());
    }

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
