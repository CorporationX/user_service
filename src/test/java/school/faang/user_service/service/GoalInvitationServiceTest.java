package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {
    User user;
    Goal goal;
    long userId;
    long goalId;
    long goalInvitationId;
    GoalInvitation goalInvitation;

    @Mock
    Goal mockGoal;

    @Mock
    GoalInvitation mockGoalInvitation;

    @Mock
    GoalInvitationMapper goalInvitationMapper;

    @Mock
    UserService userService;

    @Mock
    List<GoalInvitation> invitations;

    @Mock
    GoalRepository goalRepository;

    @Mock
    List<InvitationFilter> invitationFilters;

    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @InjectMocks
    GoalInvitationService goalInvitationService;

    @Nested
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

        @Test
        public void testRejectGoalInvitationThrowEntityExcBecOfGoalInvitation() {
            EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                    () -> goalInvitationService.rejectGoalInvitation(goalInvitationId));
            assertEquals("Invalid request. Requested goal invitation not found", exc.getMessage());
        }

        @Test
        public void testRejectGoalInvitationThrowEntityExcBecOfGoal() {
            Mockito.when(goalInvitationRepository.findById(goalInvitationId)).thenReturn(
                    Optional.of(mockGoalInvitation));
            Mockito.when(mockGoalInvitation.getGoal()).thenReturn(mockGoal);

            EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                    () -> goalInvitationService.rejectGoalInvitation(goalInvitationId));
            assertEquals("Invalid request. Requested goal not found", exc.getMessage());
        }

        @Test
        public void testGetInvitationsReturnEmptyList() {
            List<GoalInvitationDto> result = goalInvitationService.getInvitations(new InvitationFilterDto());
            assertEquals(Collections.emptyList(), result);
        }
    }

    @Nested
    class PositiveTestGroupA {
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

    @Nested
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

    @Nested
    class PositiveTestGroupC {
        long goalInvitationId;

        @BeforeEach
        public void setUp() {
            goalInvitationId = 1L;

            Mockito.when(goalInvitationRepository.findById(goalInvitationId)).thenReturn(
                    Optional.of(mockGoalInvitation));
            Mockito.when(mockGoalInvitation.getGoal()).thenReturn(mockGoal);
            Mockito.when(goalRepository.existsById(Mockito.anyLong())).thenReturn(true);

            goalInvitationService.rejectGoalInvitation(goalInvitationId);
        }

        @Test
        public void testRejectGoalInvitationCallFindById() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).findById(goalInvitationId);
        }

        @Test
        public void testRejectGoalInvitationCallExistsById() {
            Mockito.verify(goalRepository, Mockito.times(1)).existsById(Mockito.anyLong());
        }

        @Test
        public void testRejectGoalInvitationCallSave() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(mockGoalInvitation);
        }
    }

    @Nested
    class PositiveTestGroupD {
        @BeforeEach
        public void setUp() {
            goalInvitationService = new GoalInvitationService(
                    userService,
                    goalRepository,
                    invitationFilters,
                    goalInvitationMapper,
                    goalInvitationRepository);

            Mockito.when(goalInvitationRepository.findAll()).thenReturn(invitations);

            goalInvitationService.getInvitations(new InvitationFilterDto());
        }

        @Test
        public void testGetInvitationsCallFindAll() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).findAll();
        }

        @Test
        public void testGetInvitationsCallIsEmpty() {
            Mockito.verify(invitations, Mockito.times(1)).isEmpty();
        }

        @Test
        public void testGetInvitationsCallStream1() {
            Mockito.verify(invitationFilters, Mockito.times(1)).stream();
        }

        @Test
        public void testGetInvitationsCallStream2() {
            Mockito.verify(invitations, Mockito.times(1)).stream();
        }
    }
}
