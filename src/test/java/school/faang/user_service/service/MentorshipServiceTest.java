package school.faang.user_service.service;

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
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserMapperImpl userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;

    @BeforeEach
    void setUp() {
        mentor = new User();
        mentor.setId(10L);

        mentee = new User();
        mentee.setId(20L);

        UserDto userDto = new UserDto();
        userDto.setId(30L);
        userDto.setEmail("Artem");

        mentor.setMentees(List.of(mentee));
        mentee.setMentors(List.of(mentor));
    }

    @Test
    void testIfMentorNotExists() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(mentor.getId()));
    }

    @Test
    void testIfMenteeNotExists() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(mentee.getId()));
    }

    @Test
    void testGetMenteesIfMentorExists() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        List<UserDto> mentees = mentorshipService.getMentees(mentor.getId());
        assertEquals(1, mentees.size());
        verify(userMapper, times(1)).toDto(mentee);
    }

    @Test
    void testGetMenteesIfNoMenteesFound() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        mentor.setMentees(Collections.emptyList());
        List<UserDto> mentees = mentorshipService.getMentees(mentor.getId());
        assertEquals(0, mentees.size());
    }

    @Test
    void testGetMentorsIfMenteeExists() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        List<UserDto> mentors = mentorshipService.getMentors(mentee.getId());
        assertEquals(1, mentors.size());

        verify(userMapper, times(1)).toDto(mentor);
    }

    @Test
    void testGetMentorsIfNoMentorsFound() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        mentee.setMentors(Collections.emptyList());

        List<UserDto> mentors = mentorshipService.getMentors(mentee.getId());
        assertEquals(0, mentors.size());
    }

    @Test
    void testDeleteMenteeBySuccess() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());
        verify(mentorshipRepository, times(1)).delete(mentee);
    }

    @Test
    void testDeleteMentorBySuccess() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());
        verify(mentorshipRepository, times(1)).delete(mentor);
    }

    @Test
    void testDeleteMentorIfMentorNotFound() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(mentee.getId(), 999L));
        verify(mentorshipRepository, never()).delete(any());
    }

    @Test
    void testDeleteMenteeIfMenteeNotFound() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(999L, mentor.getId()));
        verify(mentorshipRepository, never()).delete(any());
    }

}
