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
            goalInvitation = new GoalInvitation();
            user = new User();
            goal = new Goal();

            goal.setId(1L);
            goalInvitation.setGoal(goal);
            goalInvitation.setInvited(user);
        }

        @Test
        public void testAcceptGoalInvitationThrowEntityExcBecOfGoalInvitation() {
            EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                    () -> goalInvitationService.acceptGoalInvitation(1L));

            assertEquals("Invalid request. Requested goal invitation not found", exc.getMessage());
        }

        @Test
        public void testAcceptGoalInvitationThrowEntityExcBecOfGoal() {
            Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

            EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                    () -> goalInvitationService.acceptGoalInvitation(1L));

            assertEquals("Invalid request. Requested goal not found", exc.getMessage());
        }

        @Test
        public void testAcceptGoalInvitationThrowIllegalArgsExcBecOfGoalsSize() {
            user.setGoals(List.of(new Goal(), new Goal(), new Goal()));

            Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
            Mockito.when(goalRepository.existsById(1L)).thenReturn(true);

            IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                    () -> goalInvitationService.acceptGoalInvitation(1L));
            assertEquals("The user already has the maximum number of goals", exc.getMessage());
        }

        @Test
        public void testAcceptGoalInvitationThrowIllegalArgsExcBecOfGoal() {
            user.setGoals(List.of(new Goal(), goal));

            Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
            Mockito.when(goalRepository.existsById(1L)).thenReturn(true);

            IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> goalInvitationService.acceptGoalInvitation(1L));
            assertEquals("The user is already working on this goal", exc.getMessage());
        }
    }

    @Nested
    @DisplayName("Позитивные тесты")
    class PositiveTestGroup {

        @BeforeEach
        public void setUp() {
            goalInvitation = new GoalInvitation();
            user = new User();
            goal = new Goal();

            goal.setId(1L);
            goalInvitation.setGoal(goal);
            goalInvitation.setInvited(user);
            user.setGoals(new ArrayList<>(List.of(new Goal(), new Goal())));

            Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
            Mockito.when(goalRepository.existsById(1L)).thenReturn(true);

            goalInvitationService.acceptGoalInvitation(1L);
        }

        @Test
        public void testAcceptGoalInvitationCallFindById() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).findById(1L);
        }

        @Test
        public void testAcceptGoalInvitationCallExistsById() {
            Mockito.verify(goalRepository, Mockito.times(1)).existsById(1L);
        }
    }
}