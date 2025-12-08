package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ConflictException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.publisher.MentorshipStartPublisher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipStartPublisher mentorshipStartPublisher;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Test
    void testAddMentorshipWhenValidIdsThenSuccess() {
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
        verify(mentorshipStartPublisher, times(1)).publish(any(MentorshipStartEvent.class));
    }

    @Test
    void testAddMentorshipWhenAlreadyExistsThenThrowsException() {
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
                ConflictException.class,
                () -> mentorshipService.addMentorship(mentorId, menteeId)
        );

        verify(mentorshipRepository, times(1)).getByIdOrThrow(mentorId);
        verify(mentorshipRepository, times(1)).getByIdOrThrow(menteeId);
        verify(mentorshipStartPublisher, never()).publish(any(MentorshipStartEvent.class));
    }

    @Test
    void testDeleteMentorshipWhenMentorshipExistsThenSuccess() {
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
    void testDeleteMentorshipWhenMentorshipNotFoundThenThrowsException() {
        long mentorId = 1L;
        long menteeId = 2L;

        User mentee = new User();
        mentee.setId(menteeId);

        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);

        assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentorship(mentorId, menteeId)
        );

        verify(mentorshipRepository, times(1)).getByIdOrThrow(menteeId);
    }

    @Test
    void testGetMenteesWhenUserHasNoMenteesThenReturnsEmptyList() {
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
    void testGetMenteesWhenUserHasMenteesThenReturnUserDtos() {
        long userId = 1L;
        long menteeId = 2L;

        User mentee = new User();
        mentee.setId(menteeId);
        mentee.setFollowers(new ArrayList<>());

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
    void testGetMentorsWhenUserHasNoMentorsThenReturnsEmptyList() {
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
    void testGetMentorsWhenUserHasMentorsThenReturnUserDtos() {
        long userId = 1L;
        long mentorId = 2L;

        User user = new User();
        user.setId(userId);

        User mentor = new User();
        mentor.setId(mentorId);
        mentor.setFollowers(new ArrayList<>());

        user.getMentors().add(mentor);

        when(mentorshipRepository.getByIdOrThrow(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertEquals(1, result.size());
        assertEquals(mentorId, result.get(0).id());

        verify(mentorshipRepository, times(1)).getByIdOrThrow(userId);
    }
}


