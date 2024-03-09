package school.faang.user_service.service;


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.exceptions.UserNotFoundException;
import school.faang.user_service.service.exceptions.messageerror.MessageError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User firstUser;
    private User thirdUser;
    private User secondUser;


    @BeforeEach
    void setUp() {
        firstUser = User.builder()
                .username("User1")
                .id(1)
                .mentees(List.of(new User(), new User(), new User()))
                .mentors(List.of(new User(), new User()))
                .build();
        secondUser = User.builder()
                .username("User2")
                .id(3)
                .mentees(List.of(firstUser, new User()))
                .mentors(List.of(new User(), new User()))
                .build();
        thirdUser = User.builder()
                .username("User3")
                .id(2)
                .mentees(new ArrayList<>(List.of(firstUser, secondUser)))
                .mentors(new ArrayList<>(List.of(firstUser, secondUser)))
                .build();
    }

    @Test
    @DisplayName("There is no such id of User in Repository")
    public void testGetMentees_UserNotFound() {
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = Assert.assertThrows(UserNotFoundException.class,
                () -> mentorshipService.getMentees(firstUser.getId()));
        Assert.assertEquals(userNotFoundException.getMessage(), MessageError.USER_NOT_FOUND_EXCEPTION.getMessage());
    }


    @Test
    @DisplayName("Test of getMentees is successful ")
    public void testGetMentees_Successful() {
        when(userRepository.findById(thirdUser.getId())).thenReturn(Optional.of(thirdUser));
        List<UserDto> mentees = mentorshipService.getMentees(thirdUser.getId());
        verify(userMapper, times(1)).toDto(thirdUser.getMentees());

        List<UserDto> expected = userMapper.toDto(thirdUser.getMentees());
        Assert.assertEquals(expected, mentees);
    }

    @Test
    @DisplayName("There is no such id of user in Repository")
    public void testGetMentors_UserNotFound() {
        when(userRepository.findById(thirdUser.getId())).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = Assert.assertThrows(UserNotFoundException.class,
                () -> mentorshipService.getMentors(thirdUser.getId()));
        Assert.assertEquals(userNotFoundException.getMessage(), MessageError.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("Test of getMentors is successful ")
    public void testGetMentors_Successful() {
        when(userRepository.findById(thirdUser.getId())).thenReturn(Optional.of(thirdUser));
        List<UserDto> mentors = mentorshipService.getMentors(thirdUser.getId());
        verify(userMapper, times(1)).toDto(thirdUser.getMentees());

        List<UserDto> expected = userMapper.toDto(thirdUser.getMentees());
        Assert.assertEquals(expected, mentors);
    }


    @Test
    @DisplayName("There is no such mentor in Repository")
    public void testDeleteMentee_MentorDoesNotExist() {
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = Assert.assertThrows(UserNotFoundException.class,
                () -> mentorshipService.deleteMentee(firstUser.getId(), thirdUser.getId()));

        Assert.assertEquals(MessageError.USER_NOT_FOUND_EXCEPTION.getMessage(), userNotFoundException.getMessage());
    }

    @Test
    @DisplayName("There is no such mentee in mentor's list of mentees")
    public void testDeleteMentee_MenteeDoesNotExistInListMentees() {
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        UserNotFoundException userNotFoundException = Assert.assertThrows(UserNotFoundException.class,
                () -> mentorshipService.deleteMentee(firstUser.getId(), thirdUser.getId()));
        Assert.assertEquals(userNotFoundException.getMessage(), MessageError.USER_NOT_FOUND_EXCEPTION.getMessage());
    }


    @Test
    @DisplayName("Mentee is deleted successful and Info is updated in DB")
    public void testDeleteMentee_MenteeIsDeleted() {
        when(userRepository.findById(thirdUser.getId())).thenReturn(Optional.of(thirdUser));

        List<UserDto> actual = mentorshipService.deleteMentee(thirdUser.getId(), firstUser.getId());

        verify(userRepository, times(1)).save(thirdUser);
        verify(userMapper, times(1)).toDto(thirdUser.getMentees());

        List<UserDto> expected = userMapper.toDto(thirdUser.getMentees());

        Assert.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("There is no such mentee in Repository")
    public void testDeleteMentor_MentorDoesNotExist() {
        when(userRepository.findById(thirdUser.getId())).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = Assert.assertThrows(UserNotFoundException.class,
                () -> mentorshipService.deleteMentor(thirdUser.getId(), firstUser.getId()));

        Assert.assertEquals(MessageError.USER_NOT_FOUND_EXCEPTION.getMessage(), userNotFoundException.getMessage());
    }

    @Test
    @DisplayName("There is no such mentor iin mentee's list of mentors")
    public void testDeleteMentor_MentorDoesNotExistInListMentors() {
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        UserNotFoundException userNotFoundException = Assert.assertThrows(UserNotFoundException.class,
                () -> mentorshipService.deleteMentor(firstUser.getId(), thirdUser.getId()));
        Assert.assertEquals(userNotFoundException.getMessage(), MessageError.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("Mentor is deleted successful and Info is updated in DB")
    public void testDeleteMentor_MentorIsDeleted() {
        when(userRepository.findById(thirdUser.getId())).thenReturn(Optional.of(thirdUser));

        List<UserDto> actual = mentorshipService.deleteMentor(thirdUser.getId(), firstUser.getId());

        verify(userRepository, times(1)).save(thirdUser);
        verify(userMapper, times(1)).toDto(thirdUser.getMentors());

        List<UserDto> expected = userMapper.toDto(thirdUser.getMentors());

        Assert.assertEquals(expected, actual);
    }

}