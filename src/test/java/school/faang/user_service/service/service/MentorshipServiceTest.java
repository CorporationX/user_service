package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Captor
    private ArgumentCaptor<User> captor;

    @Test
    public void testGetMenteesWithNotExistingMentor() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentees(1L));
        assertEquals("This mentor with id: 1 is not in the database", exception.getMessage());
    }

    @Test
    public void testGetMenteesWithGettingMentees() {
        User secondUser = User.builder().id(2L).username("second").build();
        User thirdUser = User.builder().id(3L).username("third").build();
        User firstUser = User.builder().id(1L).username("first").mentees(List.of(secondUser, thirdUser)).build();

        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstUser));
        List<UserDto> result = mentorshipService.getMentees(1L);
        assertEquals(result.get(0).getId(), secondUser.getId());
        assertEquals(result.get(0).getUsername(), secondUser.getUsername());
        assertEquals(result.get(1).getId(), thirdUser.getId());
        assertEquals(result.get(1).getUsername(), thirdUser.getUsername());
    }

    @Test
    public void testGetMentorsWithNotExistingUser() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentors(1L));
        assertEquals("This mentee with id: 1 is not in the database", exception.getMessage());
    }

    @Test
    public void testGetMentorsWithGettingMentors() {
        User secondUser = User.builder().id(2L).username("second").build();
        User thirdUser = User.builder().id(3L).username("third").build();
        User firstUser = User.builder().id(1L).username("first").mentors(List.of(secondUser, thirdUser)).build();

        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstUser));
        List<UserDto> result = mentorshipService.getMentors(1L);
        assertEquals(result.get(0).getId(), secondUser.getId());
        assertEquals(result.get(0).getUsername(), secondUser.getUsername());
        assertEquals(result.get(1).getId(), thirdUser.getId());
        assertEquals(result.get(1).getUsername(), thirdUser.getUsername());
    }

    @Test
    public void testDeleteMenteeWithNotExistingMentor() {
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(1L, 2L));
        assertEquals("Mentor with id: 2 is not in the database", exception.getMessage());
    }

    @Test
    public void testDeleteMenteeWithNotExistingMentee() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(new User()));
        var exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(1L, 2L));
        assertEquals("Mentee with id: 1 is not in the database", exception.getMessage());
    }

    @Test
    public void testDeleteMenteeSavingMentor() {
        User secondUser = User.builder().id(2L).username("second").build();
        User thirdUser = User.builder().id(3L).username("third").build();
        List<User> users = new ArrayList<>();
        users.add(secondUser);
        users.add(thirdUser);
        User firstUser = User.builder().id(1L).username("first").mentees(users).build();

        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(secondUser));
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstUser));

        mentorshipService.deleteMentee(2L, 1L);
        verify(mentorshipRepository, times(1)).save(captor.capture());

        User mentor = captor.getValue();
        assertEquals(mentor.getId(), firstUser.getId());
        assertEquals(mentor.getMentees().get(0).getId(), thirdUser.getId());
    }

    @Test
    public void testDeleteMentorWithNotExistingMentor() {
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(1L, 2L));
        assertEquals("Mentor with id: 2 is not in the database", exception.getMessage());
    }

    @Test
    public void testDeleteMentorWithNotExistingMentee() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(new User()));
        var exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(1L, 2L));
        assertEquals("Mentee with id: 1 is not in the database", exception.getMessage());
    }

    @Test
    public void testDeleteMentorSavingMentee() {
        User secondUser = User.builder().id(2L).username("second").build();
        User thirdUser = User.builder().id(3L).username("third").build();
        List<User> users = new ArrayList<>();
        users.add(secondUser);
        users.add(thirdUser);
        User mentee = User.builder().id(1L).username("first").mentors(users).build();

        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(secondUser));
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentor(1L, 2L);
        verify(mentorshipRepository, times(1)).save(captor.capture());

        User captorValue = captor.getValue();
        assertEquals(captorValue.getId(), mentee.getId());
        assertEquals(captorValue.getMentors().get(0).getId(), thirdUser.getId());
    }
}
