package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.exception.mentorship.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.message.mentorship.ExceptionMessage;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;

    @BeforeEach
    public void setUp() {
        mentor = new User();
        mentor.setId(1L);
        mentor.setMentees(new ArrayList<>());

        mentee = new User();
        mentee.setId(2L);
        mentee.setMentors(new ArrayList<>());
    }

    @Test
    public void testGetMenteesWithNonExistId() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.getMentees(mentor.getId())
        );
        assertEquals(
                ExceptionMessage.USER_NOT_FOUND.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    public void testGetMenteesWithEmptyMentees() {
        List<Long> expectedMenteeList = List.of();
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));

        List<Long> actualMenteeList = mentorshipService.getMentees(mentor.getId());

        assertIterableEquals(expectedMenteeList, actualMenteeList);
    }

    @Test
    public void testGetMenteesSuccessfully() {
        User secondMentee = new User();
        secondMentee.setId(3L);
        mentor.setMentees(List.of(mentee, secondMentee));
        List<Long> expectedMenteeList = List.of(mentee.getId(), secondMentee.getId());
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));

        List<Long> actualMenteeList = mentorshipService.getMentees(mentor.getId());

        assertIterableEquals(expectedMenteeList, actualMenteeList);
    }

    @Test
    public void testGetMentorsWithNonExistId() {
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.getMentors(mentee.getId())
        );
        assertEquals(
                ExceptionMessage.USER_NOT_FOUND.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    public void testGetMentorsWithEmptyMentors() {
        List<Long> expectedMentorsList = List.of();
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        List<Long> actualMentorList = mentorshipService.getMentors(mentee.getId());

        assertIterableEquals(expectedMentorsList, actualMentorList);
    }

    @Test
    public void testGetMentorsSuccessfully() {
        User secondMentor = new User();
        secondMentor.setId(3L);
        mentee.setMentors(List.of(mentor, secondMentor));
        List<Long> expectedMentorList = List.of(mentor.getId(), secondMentor.getId());
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        List<Long> actualMentorList = mentorshipService.getMentors(mentee.getId());

        assertIterableEquals(expectedMentorList, actualMentorList);
    }

    @Test
    public void testDeleteMenteeWithEqualIds() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipService.deleteMentee(mentor.getId(), mentor.getId())
        );
        assertEquals(
                ExceptionMessage.EQUAL_IDS.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    public void testDeleteMenteeWithNonExistMentorId() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId())
        );
        assertEquals(
                ExceptionMessage.USER_NOT_FOUND.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    public void testDeleteMenteeWithNonExistMenteeId() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId())
        );
        assertEquals(
                ExceptionMessage.USER_NOT_FOUND.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    public void testDeleteMenteeSuccessfully() {
        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);

        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());

        verify(mentorshipRepository, times(1)).saveAll(List.of(mentor, mentee));
    }

    @Test
    public void testDeleteMentorWithEqualIds() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentee.getId())
        );
        assertEquals(
                ExceptionMessage.EQUAL_IDS.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    public void testDeleteMentorWithNonExistMentorId() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId())
        );
        assertEquals(
                ExceptionMessage.USER_NOT_FOUND.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    public void testDeleteMentorWithNonExistMenteeId() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId())
        );
        assertEquals(
                ExceptionMessage.USER_NOT_FOUND.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    public void testDeleteMentorSuccessfully() {
        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);

        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());

        verify(mentorshipRepository, times(1)).saveAll(List.of(mentor, mentee));
    }
}
