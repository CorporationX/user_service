package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    private final Long userId = 2L;
    private final Long user1 = 3L;
    private final Long user2 = 4L;
    private final Long user3 = 5L;
    private final List<User> userList = List.of(
            User.builder().id(user1).build(),
            User.builder().id(user2).build(),
            User.builder().id(user3).build());
    private final User user = User.builder()
            .id(2L)
            .mentees(userList)
            .build();
    private final User user4 = User.builder()
            .id(2L)
            .mentors(userList)
            .build();

    @Test
    void shouldThrowEntityNotFoundExceptionForMentor() {
        Mockito.when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->mentorshipService.getMentees(userId));
        Mockito.verify(mentorshipRepository).findById(userId);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionForMentee() {
        Mockito.when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->mentorshipService.getMentors(userId));
        Mockito.verify(mentorshipRepository).findById(userId);
    }

    @Test
    void shouldReturnMentees(){
        Mockito.when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertEquals(userList, mentorshipService.getMentees(userId));
        Mockito.verify(mentorshipRepository, Mockito.times(2)).findById(user.getId());
    }

    @Test
    void shouldReturnMentors(){
        Mockito.when(mentorshipRepository.findById(user4.getId())).thenReturn(Optional.of(user4));
        assertEquals(userList, mentorshipService.getMentors(userId));
        Mockito.verify(mentorshipRepository, Mockito.times(2)).findById(user4.getId());
    }
}