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
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @Mock
    UserService userService;

    @Mock
    GoalRepository goalRepository;

    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @InjectMocks
    GoalInvitationService goalInvitationService;

    @Nested
    @DisplayName("Негативные тесты")
    class NegativeTestGroup {

        @Test
        public void testCreateInvitationThrowIllegalArgExc() {
            Mockito.when(userService.findUserById(1L)).thenReturn(new User());
            assertThrows(IllegalArgumentException.class, () -> goalInvitationService.createInvitation(
                    new GoalInvitationDto(1L, 1L, 1L, 1L, RequestStatus.PENDING)));
        }

        @Test
        public void testCreateInvitationThrowEntityExc() {
            Mockito.when(userService.findUserById(1L)).thenReturn(new User());
            Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> goalInvitationService.createInvitation(
                    new GoalInvitationDto(1L, 1L, 2L, 1L, RequestStatus.PENDING)));
        }
    }

    @Nested
    @DisplayName("Позитивные тесты")
    class PositiveTestGroupB {

        @BeforeEach
        public void setUp() {
            Mockito.when(userService.findUserById(1L)).thenReturn(new User());
            Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.of(new Goal()));

            goalInvitationService.createInvitation(new GoalInvitationDto(1L, 1L, 2L, 1L, RequestStatus.PENDING));
        }

        @Test
        public void testCreateInvitationCallFindUserById() {
            Mockito.verify(userService, Mockito.times(2)).findUserById(Mockito.anyLong());
        }

        @Test
        public void testCreateInvitationCallFindById() {
            Mockito.verify(goalRepository, Mockito.times(1)).findById(1L);
        }

        @Test
        public void testCreateInvitationCallSave() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(Mockito.any());
        }
    }
}