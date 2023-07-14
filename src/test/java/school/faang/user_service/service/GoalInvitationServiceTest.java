package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {
    User user;
    Goal goal;
    long userId;
    long goalId;
    long goalInvitationId;
    GoalInvitation goalInvitation;

    @Mock
    GoalRepository goalRepository;

    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @InjectMocks
    GoalInvitationService goalInvitationService;

    @Nested
    @DisplayName("Негативные тесты")
    class NegativeTestGroup {
        @BeforeEach
        public void setUp() {
            goalId = 1L;
            userId = 1L;
            goalInvitationId = 1L;

            goal = Goal.builder()
                    .id(goalId)
                    .build();
            user = User.builder()
                    .id(userId)
                    .build();
            goalInvitation = new GoalInvitation();

            goalInvitation.setGoal(goal);
            goalInvitation.setInvited(user);
        }

        @Test
        public void testAcceptGoalInvitationThrowEntityExcBecOfGoalInvitation() {
            EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                    () -> goalInvitationService.acceptGoalInvitation(goalInvitationId));
            assertEquals("Invalid request. Requested goal invitation not found", exc.getMessage());
        }

        @Test
        public void testAcceptGoalInvitationThrowEntityExcBecOfGoal() {
            Mockito.when(goalInvitationRepository.findById(goalInvitationId)).thenReturn(Optional.of(goalInvitation));

            EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                    () -> goalInvitationService.acceptGoalInvitation(goalInvitationId));
            assertEquals("Invalid request. Requested goal not found", exc.getMessage());
        }

        @Test
        public void testAcceptGoalInvitationThrowIllegalArgsExcBecOfGoalsSize() {
            user.setGoals(List.of(new Goal(), new Goal(), new Goal()));

            Mockito.when(goalInvitationRepository.findById(goalInvitationId)).thenReturn(Optional.of(goalInvitation));
            Mockito.when(goalRepository.existsById(goalId)).thenReturn(true);

            IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                    () -> goalInvitationService.acceptGoalInvitation(goalInvitationId));
            assertEquals("The user already has the maximum number of goals", exc.getMessage());
        }

        @Test
        public void testAcceptGoalInvitationThrowIllegalArgsExcBecOfGoal() {
            user.setGoals(List.of(new Goal(), goal));

            Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
            Mockito.when(goalRepository.existsById(Mockito.any())).thenReturn(true);

            IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                    () -> goalInvitationService.acceptGoalInvitation(goalInvitationId));
            assertEquals("The user is already working on this goal", exc.getMessage());
        }
    }

    @Nested
    @DisplayName("Позитивные тесты")
    class PositiveTestGroup {
        @BeforeEach
        public void setUp() {
            goalId = 1L;
            goalInvitationId = 1L;
            goalInvitation = new GoalInvitation();
            goal = Goal.builder()
                    .id(goalId)
                    .build();
            user = User.builder()
                    .goals(new ArrayList<>(List.of(new Goal(), new Goal())))
                    .build();

            goalInvitation.setGoal(goal);
            goalInvitation.setInvited(user);

            Mockito.when(goalInvitationRepository.findById(goalInvitationId)).thenReturn(Optional.of(goalInvitation));
            Mockito.when(goalRepository.existsById(goalId)).thenReturn(true);

            goalInvitationService.acceptGoalInvitation(goalInvitationId);
        }

        @Test
        public void testAcceptGoalInvitationCallFindById() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).findById(goalInvitationId);
        }

        @Test
        public void testAcceptGoalInvitationCallExistsById() {
            Mockito.verify(goalRepository, Mockito.times(1)).existsById(goalId);
        }
    }
}