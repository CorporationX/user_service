package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    MentorshipRepository mentorshipRepository;
    @InjectMocks
    MentorshipServiceImpl mentorshipService;
    private User user;
    private final Long userId = 10L;
    private final Long menteeId = 1L;
    private final Long mentorId = 3L;
    private final Long notExistingId = 20L;

    @BeforeEach
    void init() {
        User user1 = User.builder()
                .id(1L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .build();
        User user3 = User.builder()
                .id(3L)
                .build();
        User user4 = User.builder()
                .id(4L)
                .build();
        User user5 = User.builder()
                .id(5L)
                .build();

        user = User.builder()
                .mentees(List.of(user1, user2))
                .mentors(List.of(user3, user4, user5))
                .build();
    }

    @Test
    void testGetMentees() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        var result = mentorshipService.getMentees(userId);

        verify(userRepository, times(1)).findById(userId);
        assertTrue(result.containsAll(user.getMentees())
                && user.getMentees().containsAll(result)
        );
    }

    @Test
    void testGetMenteesThrowsUserNotFoundException() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.getMentees(userId)
        );
    }

    @Test
    void testGetMentors() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        var result = mentorshipService.getMentors(userId);

        verify(userRepository, times(1)).findById(userId);
        assertTrue(user.getMentors().containsAll(result)
                && result.containsAll(user.getMentors())
        );
    }

    @Test
    void testGetMentorsThrowsUserNotFoundException() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.getMentors(userId)
        );
    }

    @Test
    void deleteMentee() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        mentorshipService.deleteMentee(menteeId, userId);

        verify(userRepository, times(1)).findById(userId);
        verify(mentorshipRepository, times(1)).delete(menteeId, userId);
    }

    @Test
    void testDeleteMenteeThrowsUserNotFoundExceptionIfMentorNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentee(menteeId, userId)
        );
    }

    @Test
    void testDeleteMenteeThrowsUserNotFoundExceptionIfMenteeNotFoundInListOfMentees() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentee(notExistingId, userId)
        );
    }

    @Test
    void deleteMentor() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        mentorshipService.deleteMentor(userId, mentorId);

        verify(userRepository, times(1)).findById(userId);
        verify(mentorshipRepository, times(1)).delete(userId, mentorId);
    }

    @Test
    void testDeleteMentorThrowsUserNotFoundExceptionIfMenteeNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentor(userId, mentorId)
        );
    }

    @Test
    void testDeleteMentorThrowsUserNotFoundExceptionIfMentorNotFoundInListOfMentors() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentor(userId, notExistingId)
        );
    }
}