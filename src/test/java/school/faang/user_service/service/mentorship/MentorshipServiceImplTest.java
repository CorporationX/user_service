package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Test
    void testAddMentorship_WhenValidIds_ThenSuccess() {
        long mentorId = 1L;
        long menteeId = 2L;

        User mentor = new User();
        mentor.setId(mentorId);

        User mentee = new User();
        mentee.setId(menteeId);

        when(mentorshipRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);

        mentorshipService.addMentorship(mentorId, menteeId);

        assertEquals(1, mentee.getMentors().size());
        assertTrue(mentee.getMentors().contains(mentor));

        verify(mentorshipRepository, times(1)).getByIdOrThrow(mentorId);
        verify(mentorshipRepository, times(1)).getByIdOrThrow(menteeId);
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    @Test
    void testAddMentorship_WhenAlreadyExists_ThenThrowsException() {
        long mentorId = 1L;
        long menteeId = 2L;

        User mentor = new User();
        mentor.setId(mentorId);

        User mentee = new User();
        mentee.setId(menteeId);
        mentee.getMentors().add(mentor);

        when(mentorshipRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);

        assertThrows(
                DataValidationException.class,
                () -> mentorshipService.addMentorship(mentorId, menteeId)
        );

        verify(mentorshipRepository, times(1)).getByIdOrThrow(mentorId);
        verify(mentorshipRepository, times(1)).getByIdOrThrow(menteeId);
    }

    @Test
    void testDeleteMentorship_WhenMentorshipExists_ThenSuccess() {
        long mentorId = 1L;
        long menteeId = 2L;

        User mentor = new User();
        mentor.setId(mentorId);

        User mentee = new User();
        mentee.setId(menteeId);
        mentee.getMentors().add(mentor);

        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);

        mentorshipService.deleteMentorship(mentorId, menteeId);

        assertTrue(mentee.getMentors().isEmpty());

        verify(mentorshipRepository, times(1)).getByIdOrThrow(menteeId);
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    @Test
    void testDeleteMentorship_WhenMentorshipNotFound_ThenThrowsException() {
        long mentorId = 1L;
        long menteeId = 2L;

        User mentee = new User();
        mentee.setId(menteeId);

        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);

        assertThrows(
                DataValidationException.class,
                () -> mentorshipService.deleteMentorship(mentorId, menteeId)
        );

        verify(mentorshipRepository, times(1)).getByIdOrThrow(menteeId);
    }

    @Test
    void testGetMentees_WhenUserHasNoMentees_ThenReturnsEmptyList() {
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(mentorshipRepository.getByIdOrThrow(userId))
                .thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertTrue(result.isEmpty());

        verify(mentorshipRepository, times(1)).getByIdOrThrow(userId);
    }

    @Test
    void testGetMentees_WhenUserHasMentees_ThenReturnUserDtos() {
        long userId = 1L;
        long menteeId = 2L;

        User mentee = new User();
        mentee.setId(menteeId);

        User user = new User();
        user.setId(userId);
        user.getMentees().add(mentee);

        when(mentorshipRepository.getByIdOrThrow(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertEquals(1, result.size());
        assertEquals(menteeId, result.get(0).id());

        verify(mentorshipRepository, times(1)).getByIdOrThrow(userId);
    }

    @Test
    void testGetMentors_WhenUserHasNoMentors_ThenReturnsEmptyList() {
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(mentorshipRepository.getByIdOrThrow(userId))
                .thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertTrue(result.isEmpty());

        verify(mentorshipRepository, times(1)).getByIdOrThrow(userId);
    }

    @Test
    void testGetMentors_WhenUserHasMentors_ThenReturnUserDtos() {
        long userId = 1L;
        long mentorId = 2L;

        User user = new User();
        user.setId(userId);

        User mentor = new User();
        mentor.setId(mentorId);

        user.getMentors().add(mentor);

        when(mentorshipRepository.getByIdOrThrow(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertEquals(1, result.size());
        assertEquals(mentorId, result.get(0).id());

        verify(mentorshipRepository, times(1)).getByIdOrThrow(userId);
    }
}


