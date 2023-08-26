package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.MapperUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MapperUserDto mapperUserDto;
    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    void testGetMenteesException() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(1L));
    }

    @Test
    void testGetMenteesQuantity() {
        User mentee = User.builder().id(1L).build();
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentee));
        mentorshipService.getMentees(1L);
        verify(mentorshipRepository, times(1)).findById(1L);
        verify(mapperUserDto, times(1))
                .toDto(mentorshipRepository.findById(1L).get().getMentees());
    }

    @Test
    void testGetMentorsException() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(1L));
    }

    @Test
    void testGetMentorsQuantity() {
        User mentor = User.builder().id(1L).build();
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
        mentorshipService.getMentors(1L);
        verify(mentorshipRepository, times(1)).findById(1L);
        verify(mapperUserDto, times(1))
                .toDto(mentorshipRepository.findById(1L).get().getMentees());
    }

    @Test
    void testIfMenteeIdIsNotEqualsMentorId() {
        User mentee = User.builder().id(1L).build();
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), 2L));
    }

    @Test
    void testDeleteMenteeIllegalArgumentException() {
        User mentee = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentee));
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(1L, 1L));
    }

    @Test
    void testDeleteMenteeQuantity() {
        User mentee = User.builder().id(1L).build();
        User mentor = User.builder().id(2L).build();
        List<User> mentees = new ArrayList<>();
        mentees.add(mentee);
        mentor.setMentees(mentees);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());
        assertNotEquals(1, mentor.getMentees().size());
        verify(userRepository).save(any());
    }

    @Test
    void testDeleteMentorException() {
        User mentor = User.builder().id(2L).build();
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(4L, mentor.getId()));
    }

    @Test
    void testDeleteMentorExceptionTwo() {
        User mentor = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(1L, 1L));
    }

    @Test
    void testDeleteMentorQuantity() {
        User mentee = User.builder().id(1L).build();
        User mentor = User.builder().id(2L).build();
        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);
        mentee.setMentors(mentors);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());
        assertNotEquals(1, mentee.getMentors().size());
        verify(userRepository).save(any());
    }
}