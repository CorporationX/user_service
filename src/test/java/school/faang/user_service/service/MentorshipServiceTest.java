package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteeMapperImpl;
import school.faang.user_service.mapper.MentorMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    @Spy
    private MenteeMapperImpl menteeMapper;
    @Spy
    private MentorMapperImpl mentorMapper;

    User firstMentor;
    User secondMentee;
    User firstMentee;
    User secondMentor;
    User thirdMentee;

    @BeforeEach
    public void init() {
        secondMentee = User.builder().
                id(10L).
                build();
        firstMentee = User.builder()
                .id(11L)
                .build();
        secondMentor = User.builder()
                .id(2L)
                .build();
        firstMentor = User.builder()
                .id(1L)
                .mentees(List.of(secondMentee, firstMentee))
                .build();
        thirdMentee = User.builder().
                id(13L).
                mentors(List.of(secondMentor, firstMentor)).
                build();
    }

    @Test
    public void testGetMentees() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstMentor));

        assertEquals(firstMentor.getMentees(), mentorshipService.getMentees(1L));
    }

    @Test
    public void testGetMenteesInvalidId() {
        assertEquals(Collections.emptyList(), mentorshipService.getMentees(1L));
    }

    @Test
    public void testGetMentorsInvalidId() {
        assertEquals(Collections.emptyList(), mentorshipService.getMentors(1L));
    }

    @Test
    public void testDeleteMentee() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstMentor));
        mentorshipService.deleteMentee(10L, 1L);

        verify(mentorshipRepository, times(1)).save(firstMentor);
    }

    @Test
    public void testDeleteMentor() {
        when(mentorshipRepository.findById(13L)).thenReturn(Optional.of(thirdMentee));
        mentorshipService.deleteMentor(13L, 2L);

        verify(mentorshipRepository, times(1)).save(thirdMentee);
    }

    @Test
    public void testDeleteMenteeNotValidIdMentee() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstMentor));

        assertThrows(IllegalArgumentException.class, () -> mentorshipService.deleteMentee(16L, 1L));
    }

    @Test
    public void testDeleteMentorNotValidIdMentor() {
        when(mentorshipRepository.findById(13L)).thenReturn(Optional.of(thirdMentee));

        assertThrows(IllegalArgumentException.class, () -> mentorshipService.deleteMentor(13L, 5L));
    }
}