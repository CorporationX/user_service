package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;

    @BeforeEach
    public void setup() {
        mentor = new User();
        mentor.setId(1L);
        mentee = new User();
        mentee.setId(2L);

        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));
    }

    @Test
    public void testGetMentees() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(userMapper.toDto(mentee)).thenReturn(new UserDto(2L, "Mentee Name", "mentee@example.com"));

        List<UserDto> mentees = mentorshipService.getMentees(1L);

        assertEquals(1, mentees.size());
        assertEquals(2L, mentees.get(0).getId());
        verify(mentorshipRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteMentee() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(mentee));
        when(userMapper.toDto(mentee)).thenReturn(new UserDto(2L, "Mentee Name", "mentee@example.com"));

        Optional<UserDto> deletedMentee = mentorshipService.deleteMentee(1L, 2L);

        assertEquals(2L, deletedMentee.get().getId());
        verify(mentorshipRepository, times(1)).save(mentor);
    }

    @Test
    public void testGetMentors() {
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(mentee));
        mentee.setMentors(List.of(mentor));
        when(userMapper.toDto(mentor)).thenReturn(new UserDto(1L, "Mentor Name", "mentor@example.com"));

        List<UserDto> mentors = mentorshipService.getMentors(2L);

        assertEquals(1, mentors.size());
        assertEquals(1L, mentors.get(0).getId());
        verify(mentorshipRepository, times(1)).findById(2L);
        verify(userMapper, times(1)).toDto(mentor);
    }

    @Test
    public void testDeleteMentor() {
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(mentee));
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(userMapper.toDto(mentor)).thenReturn(new UserDto(1L, "Mentor Name", "mentor@example.com"));

        Optional<UserDto> deletedMentor = mentorshipService.deleteMentor(2L, 1L);

        assertTrue(deletedMentor.isPresent());
        assertEquals(1L, deletedMentor.get().getId());
        verify(mentorshipRepository, times(1)).findById(2L);
        verify(mentorshipRepository, times(1)).findById(1L);
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    @Test
    public void testGetMentees_MentorNotFound() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());

        List<UserDto> mentees = mentorshipService.getMentees(1L);

        assertTrue(mentees.isEmpty());
        verify(mentorshipRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteMentee_MenteeNotFound() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<UserDto> result = mentorshipService.deleteMentee(1L, 2L);

        assertTrue(result.isEmpty()); 
        verify(mentorshipRepository, times(1)).findById(1L);
        verify(mentorshipRepository, times(1)).findById(2L);
        verify(mentorshipRepository, times(0)).save(mentor); 
    }

    @Test
    public void testDeleteMentor_MentorNotFound() {
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(mentee));
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDto> result = mentorshipService.deleteMentor(2L, 1L);

        assertTrue(result.isEmpty()); 
        verify(mentorshipRepository, times(1)).findById(2L);
        verify(mentorshipRepository, times(1)).findById(1L);
        verify(mentorshipRepository, times(0)).save(mentee); 
    }
}
