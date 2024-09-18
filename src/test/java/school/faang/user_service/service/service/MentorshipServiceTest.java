package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.goal.GoalService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    @Spy
    private MentorshipMapperImpl mentorshipMapper;
    private User user;
    private User userMentee;
    private User userMentor;
    private MentorshipUserDto userDtoMentee;
    private MentorshipUserDto userDtoMentor;
    private final List<MentorshipUserDto> mentees = new ArrayList<>();
    private final List<MentorshipUserDto> mentors = new ArrayList<>();
    private static final long MENTEE_ID = 1;
    private static final long MENTOR_ID = 2;
    private static final long USER_ID = 3;

    @BeforeEach
    public void init() {
        String menteeUsername = "Messi";
        String mentorUsername = "Ronaldo";

        userMentee = User.builder()
                .id(MENTEE_ID)
                .username(menteeUsername)
                .build();
        userMentor = User.builder()
                .id(MENTOR_ID)
                .username(mentorUsername)
                .build();

        List<User> userMentees = new ArrayList<>();
        userMentees.add(userMentee);
        List<User> userMentors = new ArrayList<>();
        userMentors.add(userMentor);

        user = User.builder().id(USER_ID).mentees(userMentees).mentors(userMentors).build();
        userMentee.setMentors(new ArrayList<>(Collections.singleton(user)));
        userMentor.setMentees(new ArrayList<>(Collections.singletonList(user)));

        userDtoMentee = new MentorshipUserDto(MENTEE_ID, menteeUsername);
        userDtoMentor = new MentorshipUserDto(MENTOR_ID, mentorUsername);

        mentees.add(userDtoMentee);
        mentors.add(userDtoMentor);
    }

    @Test
    @DisplayName("Get mentees successful")
    public void testGetMenteesSuccessful() {
        when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        List<MentorshipUserDto> userMentees = mentorshipService.getMentees(USER_ID);
        assertEquals(mentees.size(), userMentees.size());
        assertTrue(userMentees.containsAll(mentees));
        Mockito.verify(mentorshipRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Get mentees failure")
    public void testGetMenteesFailure() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentees(anyLong()));
    }

    @Test
    @DisplayName("Get mentors successful")
    public void testGetMentorsFailure() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentors(anyLong()));
    }

    @Test
    @DisplayName("Get mentors failure")
    public void testGetMentorsSuccessful() {
        when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        List<MentorshipUserDto> userMentors = mentorshipService.getMentors(USER_ID);
        assertEquals(mentors.size(), userMentors.size());
        assertTrue(userMentors.containsAll(mentors));
        Mockito.verify(mentorshipRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Delete mentorship relations successful")
    public void testDeleteMentorshipRelationsSuccessful() {
        when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(mentorshipRepository.findById(MENTEE_ID)).thenReturn(Optional.of(userMentee));
        mentorshipService.deleteMentorshipRelations(USER_ID, MENTEE_ID);
        verify(mentorshipRepository).findById(USER_ID);
        verify(mentorshipRepository).findById(MENTEE_ID);
        assertFalse(user.getMentees().contains(userMentee));
        verify(mentorshipRepository).save(user);
    }

    @Test
    @DisplayName("Delete mentorship relations failure")
    public void testDeleteMentorshipRelationsFailure() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(mentorshipRepository.findById(MENTOR_ID)).thenReturn(Optional.of(userMentee));
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentorshipRelations(MENTOR_ID, anyLong()));
    }

    @Test
    @DisplayName("Should deactivate mentorship and remove mentor from mentee successfully")
    void testDeactivateMentorshipSuccess() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        mentorshipService.deactivateMentorship(USER_ID);

        // Assert
        verify(userRepository).findById(USER_ID); // Verify repository interaction
        verify(goalService).setUserGoalsToSelf(userMentee); // Verify goal service call for each mentee

        // Ensure the mentor was removed from the mentee
        assertFalse(userMentee.getMentors().contains(user), "Mentee should no longer have the mentor");
        assertTrue(userMentee.getMentors().isEmpty(), "Mentee's mentor list should be empty");

        // Ensure that the mentee list of the user is intact
        assertTrue(user.getMentees().contains(userMentee), "Mentor should still reference the mentee");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if user is not found when deactivating mentorship")
    void testDeactivateMentorshipUserNotFound() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                                                         () -> mentorshipService.deactivateMentorship(USER_ID),
                                                         "Expected NoSuchElementException to be thrown");

        // Verify that the exception is thrown due to user not being found
        assertEquals(NoSuchElementException.class, exception.getClass(), "Exception should be NoSuchElementException");
        verify(userRepository).findById(USER_ID); // Ensure repository was called
        verifyNoInteractions(goalService); // No goalService interaction
    }

    @Test
    @DisplayName("Should handle multiple mentees and remove mentor from each mentee")
    void testDeactivateMentorshipWithMultipleMentees() {
        // Arrange
        User secondMentee = User.builder().id(3L).mentors(new ArrayList<>(Collections.singletonList(user))).build();
        user.getMentees().add(secondMentee);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        mentorshipService.deactivateMentorship(USER_ID);

        // Assert
        verify(goalService).setUserGoalsToSelf(userMentee);
        verify(goalService).setUserGoalsToSelf(secondMentee);

        // Verify mentors are removed from both mentees
        assertTrue(userMentee.getMentors().isEmpty(), "First mentee should have no mentors left");
        assertTrue(secondMentee.getMentors().isEmpty(), "Second mentee should have no mentors left");
    }

    @Test
    @DisplayName("Should not modify other mentees or mentors outside the scope")
    void testDeactivateMentorshipDoesNotAffectUnrelatedEntities() {
        // Arrange
        User unrelatedMentee = User.builder().id(4L).mentors(new ArrayList<>()).build();
        user.getMentees().add(unrelatedMentee);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        mentorshipService.deactivateMentorship(USER_ID);

        // Assert
        verify(goalService).setUserGoalsToSelf(userMentee);
        assertTrue(unrelatedMentee.getMentors().isEmpty(), "Unrelated mentee should remain unaffected");
    }
}