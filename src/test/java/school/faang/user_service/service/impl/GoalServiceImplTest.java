package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.model.dto.GoalDto;
import school.faang.user_service.model.event.GoalCompletedEvent;

import school.faang.user_service.model.entity.Goal;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.repository.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @InjectMocks
    private GoalServiceImpl goalService;

    private Goal existingGoal;
    private GoalDto goalDto;

    @BeforeEach
    public void setUp() {
        existingGoal = new Goal();
        existingGoal.setId(1L);
        existingGoal.setStatus(GoalStatus.ACTIVE);

        goalDto = new GoalDto();
        goalDto.setTitle("New Goal Title");
    }

    @Test
    @DisplayName("Should update goal and publish events successfully")
    public void testUpdateGoal_Success() {
        Goal existingGoal = Goal.builder()
                .id(1L)
                .title("Original Title")
                .description("Original Description")
                .status(GoalStatus.ACTIVE)
                .mentor(User.builder().id(1L).build())
                .build();

        List<User> users = List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build()
        );

        Goal updatedGoal = Goal.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated Description")
                .status(GoalStatus.COMPLETED)
                .mentor(User.builder().id(2L).build())
                .build();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(goalMapper.toGoal(goalDto)).thenReturn(updatedGoal);
        when(goalRepository.findUsersByGoalId(1L)).thenReturn(users);

        goalService.updateGoal(1L, goalDto);

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository, times(1)).save(goalCaptor.capture());

        Goal savedGoal = goalCaptor.getValue();
        assertEquals(updatedGoal.getTitle(), savedGoal.getTitle());
        assertEquals(updatedGoal.getDescription(), savedGoal.getDescription());
        assertEquals(updatedGoal.getStatus(), savedGoal.getStatus());

        verify(skillService, times(1)).addSkillToUsers(users, 1L);
        verify(goalCompletedEventPublisher, times(2)).publish(any(GoalCompletedEvent.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if goal not found")
    public void testUpdateGoal_GoalNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(1L, goalDto));

        verify(goalRepository, never()).save(any());
        verify(skillService, never()).addSkillToUsers(anyList(), anyLong());
        verify(goalCompletedEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw DataValidationException when goal is already completed")
    void testUpdateGoal_GoalAlreadyCompleted() {
        existingGoal.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));

        assertThrows(DataValidationException.class, () -> goalService.updateGoal(1L, goalDto));

        verify(goalRepository, never()).save(any());
        verify(skillService, never()).addSkillToUsers(anyList(), anyLong());
        verify(goalCompletedEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should save updated goal to the repository")
    void testUpdateGoal_SaveUpdatedGoal() {
        Goal existingGoal = Goal.builder()
                .id(1L)
                .title("Original Title")
                .description("Original Description")
                .status(GoalStatus.ACTIVE)
                .mentor(User.builder().id(1L).build())
                .build();

        Goal updatedGoal = Goal.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated Description")
                .status(GoalStatus.COMPLETED)
                .mentor(User.builder().id(2L).build())
                .build();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(goalMapper.toGoal(goalDto)).thenReturn(updatedGoal);

        goalService.updateGoal(1L, goalDto);

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository, times(1)).save(goalCaptor.capture());

        Goal capturedGoal = goalCaptor.getValue();
        assertEquals("Updated Title", capturedGoal.getTitle());
        assertEquals("Updated Description", capturedGoal.getDescription());
        assertEquals(GoalStatus.COMPLETED, capturedGoal.getStatus());
    }
}
