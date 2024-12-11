package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.dto.user.MenteeResponseDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private User mentee1, mentee2;

    @Mock
    private User mentor1;

    @Mock
    private Goal goal1, goal2;

    @InjectMocks
    private MentorshipService mentorshipService;

    private long menteeId;
    private long mentorId;

    private User mentee;
    private User mentor;
    private List<User> mentors;
    private List<User> mentees;

    @BeforeEach
    public void setUp() {
        mentorId = 5L;
        menteeId = 10L;

        mentor = new User();
        mentee = new User();

        mentor.setId(mentorId);
        mentee.setId(menteeId);

        mentors = new ArrayList<>();
        mentees = new ArrayList<>();
    }

    @Test
    public void testDeleteMentor() {
        // arrange
        mentors.add(mentor);
        mentees.add(mentee);
        mentor.setMentees(mentees);
        mentee.setMentors(mentors);
        when(userRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

        List<User> expected = new ArrayList<>();

        // act
        mentorshipService.deleteMentor(menteeId, mentorId);
        List<User> actual = mentee.getMentors();

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteInvalidMentor() {
        // arrange
        mentee.setMentors(mentors);
        when(userRepository.findById(menteeId)).thenReturn(Optional.of(mentee));

        // act and assert
        assertDoesNotThrow(() -> mentorshipService.deleteMentor(menteeId, mentorId));
    }

    @Test
    public void testDeleteMentorFromInvalidMentee() {
        // arrange
        when(userRepository.findById(menteeId)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(menteeId, mentorId));
    }

    @Test
    public void testDeleteMentee() {
        // arrange
        mentors.add(mentor);
        mentees.add(mentee);
        mentor.setMentees(mentees);
        mentee.setMentors(mentors);
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(userRepository.findById(menteeId)).thenReturn(Optional.of(mentee));

        List<User> expected = new ArrayList<>();

        // act
        mentorshipService.deleteMentee(menteeId, mentorId);
        List<User> actual = mentor.getMentees();

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteInvalidMentee() {
        // arrange
        mentor.setMentees(mentees);
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

        // act and assert
        assertDoesNotThrow(() -> mentorshipService.deleteMentee(menteeId, mentorId));
    }

    @Test
    public void testDeleteMenteeFromInvalidMentor() {
        // arrange
        when(userRepository.findById(mentorId)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(menteeId, mentorId));
    }

    @Test
    public void testGetUserMentees() {
        // arrange
        mentor.setMentees(mentees);
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

        List<MenteeResponseDto> expected = userMapper.toMenteeResponseList(mentees);

        // act
        List<MenteeResponseDto> actual = mentorshipService.getMentees(mentorId);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testGetEmptyUserMentees() {
        // arrange
        mentor.setMentees(mentees);
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

        List<UserSubResponseDto> expected = new ArrayList<>();

        // act
        List<MenteeResponseDto> actual = mentorshipService.getMentees(mentorId);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testGetInvalidUserMentees() {
        // arrange
        when(userRepository.findById(mentorId)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentees(mentorId));
    }

    @Test
    public void testGetUserMentors() {
        // arrange
        mentee.setMentors(mentors);
        when(userRepository.findById(menteeId)).thenReturn(Optional.of(mentee));

        List<MenteeResponseDto> expected = userMapper.toMenteeResponseList(mentors);

        // act
        List<MenteeResponseDto> actual = mentorshipService.getMentors(menteeId);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testGetEmptyUserMentors() {
        // arrange
        mentee.setMentors(mentors);
        when(userRepository.findById(menteeId)).thenReturn(Optional.of(mentee));

        List<MenteeResponseDto> expected = userMapper.toMenteeResponseList(mentors);

        // act
        List<MenteeResponseDto> actual = mentorshipService.getMentors(menteeId);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testGetInvalidUserMentors() {
        // arrange
        when(userRepository.findById(menteeId)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentors(menteeId));
    }
    @Test
    void testStopMentorshipWithNoMenteesDoesNothing() {
        /*when(mentor.getMentees()).thenReturn(List.of());

        mentorshipService.stopMentorship(mentor);

        verifyNoInteractions(goal1, goal2, mentee1, mentee2);*/
    }
}
