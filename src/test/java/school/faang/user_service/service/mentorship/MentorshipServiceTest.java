package school.faang.user_service.service.mentorship;

import ch.qos.logback.core.testUtil.MockInitialContext;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.MenteeMentorOneUser;
import school.faang.user_service.exception.mentorship.UserNotFound;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees_IncorrectInputs_ShouldThrowException() {
        Mockito.when(mentorshipRepository.findUserById(Mockito.anyLong())).thenReturn(null);
        Assert.assertThrows(RuntimeException.class, () -> mentorshipService.getMentees(Mockito.anyLong()));
    }

    @Test
    public void testGetMentees_CorrectInputs_ShouldReturnEmptyList() {
        User user = User.builder()
                .mentors(Collections.emptyList())
                .mentees(Collections.emptyList())
                .build();

        Mockito.when(mentorshipRepository.findUserById(Mockito.anyLong())).thenReturn(Optional.of(user));

        List<UserDTO> userDTOS = mentorshipService.getMentees(Mockito.anyLong());

        Assertions.assertEquals(0, userDTOS.size());
    }

    @Test
    public void testGetMentees_NotFoundUser_ShouldThrowException() {
        Mockito.when(mentorshipRepository.findUserById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(UserNotFound.class, () -> mentorshipService.getMentees(Mockito.anyLong()));
    }

    @Test
    public void testGetMentors_CorrectInputs_ShouldReturnEmptyList() {
        User user = User.builder()
                .mentors(Collections.emptyList())
                .build();

        Mockito.when(mentorshipRepository.findUserById(Mockito.anyLong())).thenReturn(Optional.of(user));

        List<UserDTO> userDTOS = mentorshipService.getMentors(Mockito.anyLong());

        Assertions.assertEquals(0, userDTOS.size());
    }

    @Test
    public void testGetMentors_NotFoundUser_ShouldThrowException() {
        Mockito.when(mentorshipRepository.findUserById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(UserNotFound.class, () -> mentorshipService.getMentors(Mockito.anyLong()));
    }

    @Test
    public void testDeleteMentee_ShouldThrowException() {
        Assertions.assertThrows(MenteeMentorOneUser.class, () -> mentorshipService.deleteMentee(5, 5));
    }

    @Test
    public void testDeleteMentee_ShouldDeleteMentee() {
        User user1 = User.builder()
                .mentees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .build();
        User user2 = User.builder()
                .mentees(new ArrayList<>(List.of(user1)))
                .mentors(new ArrayList<>(List.of(user1)))
                .build();

        Mockito.when(mentorshipRepository.findUserById(1)).thenReturn(Optional.of(user1));
        Mockito.when(mentorshipRepository.findUserById(2)).thenReturn(Optional.of(user2));

        mentorshipService.deleteMentee(1, 2);
        List<User> actualMenteeList = user2.getMentees();
        List<User> expectedMenteeList = Collections.emptyList();

        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(user2);
        Assertions.assertArrayEquals(expectedMenteeList.toArray(), actualMenteeList.toArray());
    }

    @Test
    public void testDeleteMentor_ShouldThrowException() {
        Assertions.assertThrows(MenteeMentorOneUser.class, () -> mentorshipService.deleteMentor(5, 5));
    }

    @Test
    public void testDeleteMentor_ShouldDeleteMentor() {
        User mentor = User.builder()
                .mentees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .build();
        User mentee = User.builder()
                .mentors(new ArrayList<>(List.of(mentor)))
                .build();

        Mockito.when(mentorshipRepository.findUserById(1)).thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipRepository.findUserById(2)).thenReturn(Optional.of(mentor));

        mentorshipService.deleteMentor(1, 2);

        List<User> actualMentorsList = mentee.getMentors();
        List<User> expectMentorsList = Collections.emptyList();

        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentee);
        Assertions.assertArrayEquals(expectMentorsList.toArray(), actualMentorsList.toArray());
    }
}
