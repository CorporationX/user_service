package school.faang.user_service.service.mentorship;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Captor
    private ArgumentCaptor<User> captor;
    private User firstUser = new User();
    private User secondUser = new User();

    @Test
    void testInvalidUserIdForGetMentees() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentees(-12));
    }

    @Test
    void testInvalidUserIdForGetMentors() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentors(-12));
    }
    @Test
    void testNonexistentUser() {
        Mockito.when(userRepository.existsById(13L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentors(13));
    }

    @Test
    void testMenteesListNotIdentified() {
        Mockito.when(userRepository.findById(0L)).thenReturn(Optional.of(firstUser));
        Mockito.when(userRepository.existsById(0L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentees(0));
    }

    @Test
    void testMentorsListNotIdentified() {
        Mockito.when(userRepository.findById(0L)).thenReturn(Optional.of(firstUser));
        Mockito.when(userRepository.existsById(0L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentors(0));
    }

    @Test
    void testNonExistentUserMentees() {
        Mockito.when(userRepository.findById(13L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.existsById(13L)).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentees(13));
        Mockito.verify(userRepository).findById(13L);
    }

    @Test
    void testNonExistentUserMentors() {
        Mockito.when(userRepository.findById(12L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.existsById(12L)).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentors(12));
        Mockito.verify(userRepository).findById(12L);
    }

    @Test
    void testGetMenteesUserWithMentees() {
        User user = getUserWithMenteesOrMentors(true);
        user.getMentees().forEach(mentee -> {
            mentee.setMentees(new ArrayList<>());
            mentee.setMentors(new ArrayList<>());
        });
        Mockito.when(userRepository.findById(8L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsById(8L)).thenReturn(true);

        List<UserDto> mentees = mentorshipService.getMentees(8);
        List<UserDto> users = user.getMentees().stream()
                .map(user1 -> userMapper.toDto(user1)).toList();

        Mockito.verify(userRepository).findById(8L);
        assertIterableEquals(users, mentees);

    }

    @Test
    void testGetMentorsUserWithMentors() {
        User user = getUserWithMenteesOrMentors(false);
        user.getMentors().forEach(mentee -> {
            mentee.setMentees(new ArrayList<>());
            mentee.setMentors(new ArrayList<>());
        });

        Mockito.when(userRepository.findById(8L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsById(8L)).thenReturn(true);

        List<UserDto> mentors = mentorshipService.getMentors(8);
        List<UserDto> users = user.getMentors().stream()
                .map(user1 -> userMapper.toDto(user1)).toList();

        Mockito.verify(userRepository).findById(8L);
        assertIterableEquals(users, mentors);
    }

    @Test
    void testGetMenteesUserWithoutMentees() {
        firstUser.setMentees(List.of());
        Mockito.when(userRepository.findById(9L)).thenReturn(Optional.of(firstUser));
        Mockito.when(userRepository.existsById(9L)).thenReturn(true);

        List<UserDto> mentees = mentorshipService.getMentees(9);

        Mockito.verify(userRepository).findById(9L);
        assertEquals(mentees.size(), firstUser.getMentees().size());
    }

    @Test
    void testGetMentorsUserWithoutMentors() {
        firstUser.setMentors(List.of());
        Mockito.when(userRepository.findById(9L)).thenReturn(Optional.of(firstUser));
        Mockito.when(userRepository.existsById(9L)).thenReturn(true);

        List<UserDto> mentors = mentorshipService.getMentors(9);

        Mockito.verify(userRepository).findById(9L);
        assertEquals(mentors.size(), firstUser.getMentors().size());
    }

    @Test
    void testInvalidMenteeIdForDeleteMentee() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentee(-12, 2));
    }

    @Test
    void testInvalidMentorIdForDeleteMentee() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentee(2, -2));
    }

    @Test
    void testInvalidMenteeIdForDeleteMentor() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentor(-12, 2));
    }

    @Test
    void testInvalidMentorIdForDeleteMentor() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentor(2, -2));
    }

    @Test
    void testNonExistentUserForDeleteMentee() {
        Mockito.when(userRepository.findById(6L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.existsById(6L)).thenReturn(true);
        Mockito.when(userRepository.existsById(17L)).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(17, 6));
        Mockito.verify(userRepository).findById(6L);
    }

    @Test
    void testNonExistentUserForDeleteMentor() {
        Mockito.when(userRepository.findById(6L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.existsById(6L)).thenReturn(true);
        Mockito.when(userRepository.existsById(17L)).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(6, 17));
        Mockito.verify(userRepository).findById(6L);
    }

    @Test
    void testInvalidMentorsValueForDeleteMentor() {
        Mockito.when(userRepository.findById(5L)).thenReturn(Optional.of(firstUser));
        Mockito.when(userRepository.existsById(5L)).thenReturn(true);
        Mockito.when(userRepository.existsById(17L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentor(5, 17));
        Mockito.verify(userRepository).findById(5L);
    }

    @Test
    void testInvalidMenteesValueForDeleteMentees() {
        Mockito.when(userRepository.findById(5L)).thenReturn(Optional.of(firstUser));
        Mockito.when(userRepository.existsById(5L)).thenReturn(true);
        Mockito.when(userRepository.existsById(14L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentee(14, 5));
        Mockito.verify(userRepository).findById(5L);
    }

    @Test
    void testSuccessfulDeleteMentee() {
        firstUser = getUserWithMenteesOrMentors(true);
        firstUser.setUsername("TestUser");
        secondUser.setId(5L);
        secondUser.setUsername("Mentee");
        firstUser.getMentees().add(secondUser);

        Mockito.when(userRepository.findById(4L)).thenReturn(Optional.of(firstUser));
        Mockito.when(userRepository.existsById(4L)).thenReturn(true);
        Mockito.when(userRepository.existsById(secondUser.getId())).thenReturn(true);

        mentorshipService.deleteMentee(secondUser.getId(), 4);
        Mockito.verify(userRepository).findById(4L);
        assertEquals(firstUser.getMentees().size(), 2);

        Mockito.verify(userRepository).save(captor.capture());
        assertEquals(firstUser.getUsername(), captor.getValue().getUsername());
    }

    @Test
    void testSuccessfulDeleteMentor() {
        firstUser = getUserWithMenteesOrMentors(false);
        firstUser.setUsername("TestUser");
        secondUser.setId(4L);
        secondUser.setUsername("Mentor");
        firstUser.getMentors().add(secondUser);

        Mockito.when(userRepository.findById(5L)).thenReturn(Optional.of(firstUser));
        Mockito.when(userRepository.existsById(5L)).thenReturn(true);
        Mockito.when(userRepository.existsById(secondUser.getId())).thenReturn(true);

        mentorshipService.deleteMentor(5L, secondUser.getId());
        Mockito.verify(userRepository).findById(5L);
        assertEquals(firstUser.getMentors().size(), 2);

        Mockito.verify(userRepository).save(captor.capture());
        assertEquals(firstUser.getUsername(), captor.getValue().getUsername());
    }

    private User getUserWithMenteesOrMentors(boolean mentees) {
        User firstMentees = setInfo();
        User secondMentees = setInfo();

        firstMentees.setUsername("User1");
        firstMentees.setId(0L);
        secondMentees.setUsername("User2");
        secondMentees.setId(1L);

        User user = new User();
        if (mentees) {
            user.setMentees(new ArrayList<>(List.of(firstMentees, secondMentees)));
        } else {
            user.setMentors(new ArrayList<>(List.of(firstMentees, secondMentees)));
        }
        return user;
    }

    private User setInfo() {
        User user = new User();
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setAboutMe("BlaBlaBla");
        user.setFollowees(new ArrayList<>());
        user.setFollowers(new ArrayList<>());
        user.setGoals(new ArrayList<>());
        user.setSkills(new ArrayList<>());

        return user;
    }
}