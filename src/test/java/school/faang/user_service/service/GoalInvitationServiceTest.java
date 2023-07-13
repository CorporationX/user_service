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
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {
    @Mock
    Goal goal;

    @Mock
    GoalInvitation goalInvitation;

    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @Mock
    GoalRepository goalRepository;

    @InjectMocks
    GoalInvitationService goalInvitationService;

    @Nested
    @DisplayName("Негативные тесты")
    class NegativeTestGroup {
        @Test
        public void testRejectGoalInvitationThrowEntityExcBecOfGoalInvitation() {
            EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                    () -> goalInvitationService.rejectGoalInvitation(1L));
            assertEquals("Invalid request. Requested goal invitation not found", exc.getMessage());
        }

        @Test
        public void testRejectGoalInvitationThrowEntityExcBecOfGoal() {
            Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
            Mockito.when(goalInvitation.getGoal()).thenReturn(goal);

            EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                    () -> goalInvitationService.rejectGoalInvitation(1L));
            assertEquals("Invalid request. Requested goal not found", exc.getMessage());
        }
    }

    @Nested
    @DisplayName("Позитивные тесты")
    class PositiveTestGroup {
        @BeforeEach
        public void setUp() {
            Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
            Mockito.when(goalInvitation.getGoal()).thenReturn(goal);
            Mockito.when(goalRepository.existsById(Mockito.anyLong())).thenReturn(true);

            goalInvitationService.rejectGoalInvitation(1L);
        }

        @Test
        public void testRejectGoalInvitationCallFindById() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).findById(1L);
        }

        @Test
        public void testRejectGoalInvitationCallExistsById() {
            Mockito.verify(goalRepository, Mockito.times(1)).existsById(Mockito.anyLong());
        }

        @Test
        public void testRejectGoalInvitationCallSave() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(goalInvitation);
        }
    }
}