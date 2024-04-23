package school.faang.user_service.service.mentorship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.dto.messagebroker.GoalSetEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.MenteeMapperImpl;
import school.faang.user_service.mapper.MentorMapperImpl;
import school.faang.user_service.publisher.GoalSetEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private GoalSetEventPublisher goalSetEventPublisher;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    @Spy
    private MenteeMapperImpl menteeMapper;
    @Spy
    private MentorMapperImpl mentorMapper;

    User firstMentor;
    User secondMentee;
    User firstMentee;
    User secondMentor;
    User thirdMentee;

    @BeforeEach
    public void init() {
        secondMentee = User.builder().
                id(10L).
                build();
        firstMentee = User.builder()
                .id(11L)
                .build();
        secondMentor = User.builder()
                .id(2L)
                .build();
        firstMentor = User.builder()
                .id(1L)
                .mentees(List.of(secondMentee, firstMentee))
                .build();
        thirdMentee = User.builder().
                id(13L).
                mentors(List.of(secondMentor, firstMentor)).
                build();
    }

    @Test
    public void testGetMentees() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstMentor));

        assertEquals(firstMentor.getMentees(), mentorshipService.getMentees(1L));
    }

    @Test
    public void testGetMenteesInvalidId() {
        assertThrows(EntityNotFoundException.class,() -> mentorshipService.getMentees(1L));
    }
    @Test
    public void testGetMenteesEmptyList(){
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(secondMentor));
        assertEquals(Collections.emptyList(), mentorshipService.getMentees(2L));
    }

    @Test
    public void testGetMentorsInvalidId() {
        assertThrows(EntityNotFoundException.class,()-> mentorshipService.getMentors(1L));
    }
    @Test
    public void testGetMentorsEmptyList(){
        when(mentorshipRepository.findById(10L)).thenReturn(Optional.of(secondMentee));
        assertEquals(Collections.emptyList(), mentorshipService.getMentors(10L));
    }

    @Test
    public void testDeleteMentee() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstMentor));
        when(mentorshipRepository.findById(10L)).thenReturn(Optional.of(secondMentee));
        mentorshipService.deleteMentee(10L, 1L);

        verify(mentorshipRepository, times(1)).save(firstMentor);
    }

    @Test
    public void testDeleteMentor() {
        when(mentorshipRepository.findById(13L)).thenReturn(Optional.of(thirdMentee));
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(secondMentor));
        mentorshipService.deleteMentor(13L, 2L);

        verify(mentorshipRepository, times(1)).save(thirdMentee);
    }
    @Test
    public void testDeleteMenteeIsNotOnTheList(){
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstMentor));
        when(mentorshipRepository.findById(16L)).thenReturn(Optional.of(User.builder()
                .id(16L).build()));
        assertThrows(IllegalArgumentException.class,() ->mentorshipService.deleteMentee(16L, 1L));
    }

    @Test
    public void testDeleteMenteeNotValidIdMentee() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstMentor));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(16L, 1L));
    }

    @Test
    public void testDeleteMentorNotValidIdMentor() {
        when(mentorshipRepository.findById(13L)).thenReturn(Optional.of(thirdMentee));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(13L, 5L));
    }
    @Test
    public void testDeleteMentorIsNotOnTheList(){
        when(mentorshipRepository.findById(13L)).thenReturn(Optional.of(thirdMentee));
        when(mentorshipRepository.findById(5L)).thenReturn(Optional.of(User.builder()
                .id(5L).build()));
        assertThrows(IllegalArgumentException.class,() ->mentorshipService.deleteMentor(13L, 5L));
    }

    @Test
    public void testDeleteMenteeNotValidIdMentor() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(firstMentor));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(10L, 1L));
    }

    @Test
    public void testDeleteMentorNotValidIdMentee() {
        lenient().when(mentorshipRepository.findById(13L)).thenReturn(Optional.of(thirdMentee));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(16L, 1L));
    }
    @Test
    void addGoalToUserTest(){
        long menteeId = 11L;
        long goalId = 1L;
        long mentorId = 2L;
        Goal goalTest = Goal.builder()
                .id(goalId)
                .title("goalTest")
                .build();
        MenteeDto menteeDtoResult = MenteeDto.builder().id(menteeId)
                .id(menteeId)
                .mentorsIds(List.of(mentorId))
                .goalIds(List.of(goalId))
                .build();
        GoalSetEvent goalSetEvent = new GoalSetEvent(menteeId,goalId);
        firstMentee.setMentors(List.of(secondMentor));
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(firstMentee));
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(secondMentor));
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goalTest));

        assertEquals(menteeDtoResult,mentorshipService.addGoalToMenteeFromMentor(menteeId,goalId,mentorId));
        verify(mentorshipRepository).save(firstMentee);
        verify(goalSetEventPublisher).publish(goalSetEvent);
    }
    @Test
    void addGoalToUserTestMenteeDoesNotMentors() {
        long menteeId = 1L;
        long goalId = 1L;
        long mentorId = 1L;
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(firstMentee));
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(firstMentor));

        assertThrows(NullPointerException.class,()-> mentorshipService.addGoalToMenteeFromMentor(menteeId,goalId,mentorId));
    }
    @Test
    void addGoalToUserTestMenteeDoesNotThisMentors() {
        long menteeId = 1L;
        long goalId = 1L;
        long mentorId = 2L;
        firstMentee.setMentors(List.of(secondMentor));
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(firstMentee));
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(firstMentor));

        assertThrows(IllegalArgumentException.class,()-> mentorshipService.addGoalToMenteeFromMentor(menteeId,goalId,mentorId));
    }
}