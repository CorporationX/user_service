package school.faang.user_service.service.goal;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goalInvitation.GoalInvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    private static final int ONE_TIMES_CALL_METHOD = 1;
    private static final int TWO_TIMES_CALL_METHOD = 2;

    private static final Long GOAL_INVITATION_ID_IS_ONE = 1L;
    private static final Long GOAL_INVITATION_ID_IS_TWO = 2L;
    private static final Long USER_ID_IS_ONE = 1L;
    private static final Long USER_ID_IS_TWO = 2L;
    private static final Long GOAL_ID_IS_ONE = 1L;
    private static final Long GOAL_ID_IS_TWO = 2L;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalValidator goalValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    @Mock
    private GoalInvitationFilter goalInvitationFilter;

    private GoalInvitation goalInvitation;
    private GoalInvitationDto goalInvitationDto;
    private GoalInvitationFilterDto goalInvitationFilterDto;
    private List<GoalInvitationFilter> goalInvitationFilterList;

    @Nested
    class PositiveTests {

        private User user;
        private List<GoalInvitation> goalInvitations;

        @BeforeEach
        void init() {
            user = User.builder()
                    .id(USER_ID_IS_ONE)
                    .build();
            User userWithIdTwo = User.builder()
                    .id(USER_ID_IS_TWO)
                    .build();

            goalInvitations = List.of(
                    GoalInvitation.builder()
                            .invited(user)
                            .inviter(user)
                            .build(),
                    GoalInvitation.builder()
                            .invited(userWithIdTwo)
                            .inviter(userWithIdTwo)
                            .build());
            List<User> users = new ArrayList<>();
            users.add(User.builder()
                    .id(USER_ID_IS_ONE)
                    .build());

            goalInvitationDto = GoalInvitationDto.builder()
                    .inviterUserId(USER_ID_IS_ONE)
                    .invitedUserId(USER_ID_IS_TWO)
                    .goalId(GOAL_ID_IS_ONE)
                    .build();

            goalInvitation = GoalInvitation.builder()
                    .id(GOAL_INVITATION_ID_IS_ONE)
                    .inviter(User.builder()
                            .id(USER_ID_IS_ONE)
                            .build())
                    .invited(User.builder()
                            .id(USER_ID_IS_TWO)
                            .build())
                    .goal(Goal.builder()
                            .id(GOAL_ID_IS_ONE)
                            .users(users)
                            .build())
                    .build();

        }

        @Test
        @DisplayName("DeleteAll cals ones if list size is 2")
        void whenGoalInvitationsSizeIsTwoThenTwoTimesUsesRepository() {
            goalInvitationService.deleteGoalInvitations(goalInvitations);

            verify(goalInvitationRepository, times(ONE_TIMES_CALL_METHOD))
                    .deleteAll(goalInvitations);
        }

        @Test
        @DisplayName("DeleteAllById calls ones if one of the input sheet values matches the filter")
        void whenUserExistsThenSuccess() {
            goalInvitationService.deleteGoalInvitationForUser(goalInvitations, user);

            verify(goalInvitationRepository)
                    .deleteAllById(anyCollection());
        }


        @Test
        @DisplayName("Success if inviter and invented have different ids")
        void whenInviterAndInvitedAreNotEqualsThenSuccess() {
            when(goalInvitationMapper.toEntity(goalInvitationDto))
                    .thenReturn(goalInvitation);

            goalInvitationService.createInvitation(goalInvitationDto);

            verify(userValidator, times(TWO_TIMES_CALL_METHOD))
                    .validateUserIdIsPositiveAndNotNull(anyLong());
            verify(userValidator, times(TWO_TIMES_CALL_METHOD))
                    .validateUserIsExisted(anyLong());
            verify(userValidator)
                    .validateFirstUserIdAndSecondUserIdNotEquals(
                            goalInvitationDto.getInviterUserId(),
                            goalInvitationDto.getInvitedUserId(),
                            "Inviter and invited cannot be equal");
            verify(goalValidator)
                    .validateGoalIdIsPositiveAndNotNull(goalInvitationDto.getGoalId());
            verify(goalValidator)
                    .validateGoalWithIdIsExisted(goalInvitationDto.getGoalId());
            verify(goalInvitationMapper)
                    .toEntity(any());
            verify(goalInvitationRepository)
                    .save(goalInvitation);
        }

        @Test
        @DisplayName("Update entity if goal existed")
        void whenGoalExistsThenSuccessAccept() {
            when(goalInvitationRepository.findById(GOAL_INVITATION_ID_IS_ONE))
                    .thenReturn(Optional.of(goalInvitation));

            goalInvitationService.acceptGoalInvitation(GOAL_INVITATION_ID_IS_ONE);

            verify(goalValidator)
                    .validateGoalIdIsPositiveAndNotNull(goalInvitation.getGoal().getId());
            verify(goalValidator)
                    .validateGoalWithIdIsExisted(goalInvitation.getGoal().getId());
            verify(goalValidator)
                    .validateUserActiveGoalsAreLessThenIncoming(eq(goalInvitation.getInvited().getId()),
                            anyInt());
            verify(goalValidator)
                    .validateUserNotWorkingWithGoal(goalInvitation.getInvited().getId(),
                            goalInvitation.getGoal().getId());
            verify(goalInvitationRepository)
                    .save(goalInvitation);
        }

        @Test
        @DisplayName("Update entity if goal existed")
        void whenGoalExistsThenSuccessReject() {
            when(goalInvitationRepository.findById(GOAL_INVITATION_ID_IS_ONE))
                    .thenReturn(Optional.of(goalInvitation));

            goalInvitationService.rejectGoalInvitation(GOAL_INVITATION_ID_IS_ONE);

            verify(goalValidator)
                    .validateGoalIdIsPositiveAndNotNull(goalInvitation.getGoal().getId());
            verify(goalValidator)
                    .validateGoalWithIdIsExisted(goalInvitation.getGoal().getId());
            verify(goalInvitationRepository)
                    .save(goalInvitation);
        }

        @Nested
        class GetInvitationsMethod {

            private List<GoalInvitation> goalInvitations;

            @BeforeEach
            void init() {
                goalInvitations = List.of(goalInvitation,
                        GoalInvitation.builder()
                                .id(GOAL_INVITATION_ID_IS_TWO)
                                .inviter(User.builder().id(USER_ID_IS_TWO).build())
                                .invited(User.builder().id(USER_ID_IS_ONE).build())
                                .goal(Goal.builder().id(GOAL_ID_IS_TWO).build())
                                .build());

                goalInvitationFilterDto = GoalInvitationFilterDto.builder().
                        inviterId(USER_ID_IS_ONE)
                        .build();

                goalInvitationFilterList = List.of(goalInvitationFilter);
                goalInvitationService = new GoalInvitationService(goalValidator,
                        userValidator,
                        goalInvitationMapper,
                        goalInvitationRepository,
                        goalInvitationFilterList);
            }

            @Test
            @DisplayName("Success if filter is null")
            void whenFilterIsNullThenSuccess() {
                when(goalInvitationRepository.findAll())
                        .thenReturn(goalInvitations);

                goalInvitationService.getInvitations(null);

                verify(goalInvitationRepository)
                        .findAll();
                verify(goalInvitationMapper)
                        .toDtos(goalInvitations);
            }

            @Test
            @DisplayName("Success if filter not null")
            void whenFilterIsNotNullThenSuccess() {
                when(goalInvitationRepository.findAll())
                        .thenReturn(goalInvitations);
                when(goalInvitationFilterList.get(0).isApplicable(goalInvitationFilterDto))
                        .thenReturn(true);
                when(goalInvitationFilterList.get(0).apply(any(), eq(goalInvitationFilterDto)))
                        .thenReturn(goalInvitations.stream().filter(goalInvitation ->
                                goalInvitation.getInviter().getId().equals(goalInvitationFilterDto.getInviterId())));

                goalInvitationService.getInvitations(goalInvitationFilterDto);

                verify(goalInvitationRepository)
                        .findAll();
                verify(goalInvitationMapper)
                        .toDtos(List.of(goalInvitation));
            }
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Throws ValidationException if goal doesn't exist")
        void whenGoalNotExistsInAcceptGoalInvitationThenThrowValidationException() {
            when(goalInvitationRepository.findById(GOAL_ID_IS_ONE))
                    .thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> goalInvitationService.acceptGoalInvitation(GOAL_ID_IS_ONE),
                    "No goal with id " + GOAL_ID_IS_ONE + " found");
        }

        @Test
        @DisplayName("Throws ValidationException if goal doesn't exist")
        void whenGoalNotExistsInRejectGoalInvitationThenThrowValidationException() {
            when(goalInvitationRepository.findById(GOAL_ID_IS_ONE))
                    .thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> goalInvitationService.rejectGoalInvitation(GOAL_ID_IS_ONE),
                    "No goal with id " + GOAL_ID_IS_ONE + " found");
        }
    }
}