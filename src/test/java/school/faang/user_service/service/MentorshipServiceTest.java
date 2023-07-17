package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Spy
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    private Long userId = 1L;
    private Long user1 = 3L;
    private Long user2 = 2L;
    private Long user3 = 1L;
    private List<User> userList = new ArrayList<>();

    private List<User> userList1 = new ArrayList<>();

    private User user = User.builder()
            .id(1L)
            .build();
    private User user4 = User.builder()
            .id(2L)
            .build();

    @BeforeEach
    void init(){
        userList.add(User.builder().id(user1).build());
        userList.add(User.builder().id(user2).build());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionForMentor() {
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMenteesOfUser(userId));
        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionForMentee() {
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentorsOfUser(userId));
        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    void shouldReturnMentees() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        user.setMentees(userList);
        assertEquals(userList, mentorshipService.getMenteesOfUser(user.getId()));
        Mockito.verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldReturnMentors() {
        Mockito.when(userRepository.findById(user4.getId())).thenReturn(Optional.of(user4));
        user4.setMentors(userList);
        assertEquals(userList, mentorshipService.getMentorsOfUser(user4.getId()));
        Mockito.verify(userRepository).findById(user4.getId());
    }

    @Test
    void shouldThrowExceptionForMentor_MenteeCheck(){
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()-> mentorshipService.deleteMentor(userId ,user1));
    }
    @Test
    void shouldThrowExceptionForMentor_MentorCheck(){
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()-> mentorshipService.deleteMentor(userId ,user1));
    }

    @Test
    void shouldThrowExceptionForMentor_EqualsMentorAndMenteeCheck(){
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> mentorshipService.deleteMentor(userId, userId));
        Mockito.verify(userRepository, Mockito.times(2)).findById(user.getId());
    }

    @Test
    void shouldSaveMenteeList(){
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(user4.getId())).thenReturn(Optional.of(user4));
        userList1.add(user4);
        user.setMentors(userList1);
        mentorshipService.deleteMentor(user.getId(),user4.getId());
        assertNotEquals(1, user.getMentors().size());
        Mockito.verify(userRepository).save(any());
    }

    @Test
    void shouldThrowExceptionForMentee_MenteeCheck(){
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()-> mentorshipService.deleteMentee(userId ,user1));
    }
    @Test
    void shouldThrowExceptionForMentee_MentorCheck(){
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()-> mentorshipService.deleteMentee(userId ,user1));
    }

    @Test
    void shouldThrowExceptionForMentee_EqualsMentorAndMenteeCheck(){
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> mentorshipService.deleteMentee(userId, userId));
        Mockito.verify(userRepository, Mockito.times(2)).findById(user.getId());
    }

    @Test
    void shouldSaveMentorList(){
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(user4.getId())).thenReturn(Optional.of(user4));
        userList1.add(user);
        user4.setMentees(userList1);
        mentorshipService.deleteMentee(user.getId(),user4.getId());
        assertNotEquals(1, user4.getMentees().size());
        Mockito.verify(userRepository).save(any());
    }
}