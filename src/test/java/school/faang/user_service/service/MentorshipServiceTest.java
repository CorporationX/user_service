package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.exception.mentorship.NoUserMenteeException;
import school.faang.user_service.exception.mentorship.NoUserMentorException;
import school.faang.user_service.exception.mentorship.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.message.mentorship.ExceptionMessage;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of MentorshipServiceTest")
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
    @DisplayName("getMentees - non-exist user ID")
    public void testGetMenteesWithNonExistId() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.getMentees(mentor.getId())
        );

        assertEquals(
                String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), mentor.getId()),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("getMentees - user without mentees")
    public void testGetMenteesWithEmptyMentees() {
        List<MentorshipUserDto> expectedMenteeList = List.of();
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));

        List<MentorshipUserDto> actualMenteeList = mentorshipService.getMentees(mentor.getId());

        assertNotNull(actualMenteeList);
        assertIterableEquals(expectedMenteeList, actualMenteeList);
    }

    @Test
    @DisplayName("getMentees - successfully")
    public void testGetMenteesSuccessfully() {
        User secondMentee = new User();
        secondMentee.setId(3L);
        List<User> mentees = List.of(mentee, secondMentee);
        mentor.setMentees(mentees);

        List<MentorshipUserDto> expectedMenteeList = mentorshipMapper.toDtoList(mentees);
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));

        List<MentorshipUserDto> actualMenteeList = mentorshipService.getMentees(mentor.getId());

        assertNotNull(actualMenteeList);
        assertIterableEquals(expectedMenteeList, actualMenteeList);
    }

    @Test
    @DisplayName("getMentors - non-exist user ID")
    public void testGetMentorsWithNonExistId() {
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.getMentors(mentee.getId())
        );

        assertEquals(
                String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), mentee.getId()),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("getMentors - user without mentors")
    public void testGetMentorsWithEmptyMentors() {
        List<MentorshipUserDto> expectedMentorsList = List.of();
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        List<MentorshipUserDto> actualMentorList = mentorshipService.getMentors(mentee.getId());

        assertNotNull(actualMentorList);
        assertIterableEquals(expectedMentorsList, actualMentorList);
    }

    @Test
    @DisplayName("getMentors - successfully")
    public void testGetMentorsSuccessfully() {
        User secondMentor = new User();
        secondMentor.setId(3L);
        List<User> mentors = List.of(mentor, secondMentor);
        mentee.setMentors(mentors);

        List<MentorshipUserDto> expectedMentorList = mentorshipMapper.toDtoList(mentors);
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        List<MentorshipUserDto> actualMentorList = mentorshipService.getMentors(mentee.getId());

        assertNotNull(actualMentorList);
        assertIterableEquals(expectedMentorList, actualMentorList);
    }

    @Test
    @DisplayName("deleteMentee - equal user IDs")
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
    @DisplayName("deleteMentee - non-exist mentor ID")
    public void testDeleteMenteeWithNonExistMentorId() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId())
        );

        assertEquals(
                String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), mentor.getId()),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("deleteMentee - exist mentor ID and non-exist mentee ID")
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
                String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), mentee.getId()),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("deleteMentee - mentor without such a mentee")
    public void testDeleteMenteeWithoutUserMentee() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        Exception exception = assertThrows(
                NoUserMenteeException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId())
        );

        assertEquals(ExceptionMessage.NO_USER_MENTEE.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("deleteMentee - successfully")
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
    @DisplayName("deleteMentor - equal user IDs")
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
    @DisplayName("deleteMentor - non-exist mentor ID")
    public void testDeleteMentorWithNonExistMentorId() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId())
        );

        assertEquals(
                String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), mentor.getId()),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("deleteMentor - exist mentor ID and non-exist mentee ID")
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
                String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), mentee.getId()),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("deleteMentor - mentee without such a mentor")
    public void testDeleteMentorsWithoutUserMentor() {
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        Exception exception = assertThrows(
                NoUserMentorException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId())
        );

        assertEquals(ExceptionMessage.NO_USER_MENTOR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("deleteMentor - successfully")
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
