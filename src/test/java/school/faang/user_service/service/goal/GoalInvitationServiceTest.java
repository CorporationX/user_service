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
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

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
    private UserService userService;
    @Mock
    private List<GoalInvitationFilter> goalInvitationFilterList;

    private final Long LONG_POSITIVE_VALUE_IS_ONE = 1L;
    private final Long LONG_POSITIVE_VALUE_IS_TWO = 2L;

    private GoalInvitationDto goalInvitationDto;
    private GoalInvitation goalInvitation;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации если приглашаемый и приглашенный имеют одинаковые id")
        void When_InviterAndInvitedAreEquals_Then_ThrowValidationException() {
            GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
            goalInvitationDto.setInviterId(LONG_POSITIVE_VALUE_IS_ONE);
            goalInvitationDto.setInvitedUserId(LONG_POSITIVE_VALUE_IS_ONE);

            assertThrows(ValidationException.class,
                    () -> goalInvitationService.createInvitation(goalInvitationDto),
                    "Inviter and invited cannot be equal");
        }

        @Test
        @DisplayName("Ошибка валидации если цели не существует")
        void When_GoalNotExists_Then_ThrowValidationException() {
            when(goalInvitationRepository.findById(LONG_POSITIVE_VALUE_IS_ONE))
                    .thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> goalInvitationService.acceptGoalInvitation(LONG_POSITIVE_VALUE_IS_ONE),
                    "No goal with id " + LONG_POSITIVE_VALUE_IS_ONE + " found");
        }
    }

    @Nested
    class PositiveTests {

        @BeforeEach
        void init() {
            goalInvitationDto = new GoalInvitationDto();
            goalInvitationDto.setInviterId(LONG_POSITIVE_VALUE_IS_ONE);
            goalInvitationDto.setInvitedUserId(LONG_POSITIVE_VALUE_IS_TWO);

            goalInvitation = new GoalInvitation();

            User user = new User();
            user.setId(LONG_POSITIVE_VALUE_IS_ONE);

            User anotherUser = new User();
            anotherUser.setId(LONG_POSITIVE_VALUE_IS_TWO);

            Goal goal = new Goal();
            goal.setId(LONG_POSITIVE_VALUE_IS_ONE);

            goalInvitation.setId(LONG_POSITIVE_VALUE_IS_ONE);
            goalInvitation.setInviter(user);
            goalInvitation.setInvited(anotherUser);
            goalInvitation.setGoal(goal);
        }

        @Test
        @DisplayName("Успех если приглашаемый и приглашенный имеют разные id")
        void When_InviterAndInvitedAreNotEquals_Then_Success() {
            when(goalInvitationMapper.toEntity(goalInvitationDto))
                    .thenReturn(goalInvitation);

            goalInvitationService.createInvitation(goalInvitationDto);

            verify(goalInvitationRepository, times(1))
                    .save(goalInvitation);
        }

        @Test
        @DisplayName("Обновляем сущность если цель существует")
        void When_GoalExists_Then_SuccessAccept() {
            when(goalInvitationRepository.findById(LONG_POSITIVE_VALUE_IS_ONE))
                    .thenReturn(Optional.of(goalInvitation));

            goalInvitationService.acceptGoalInvitation(LONG_POSITIVE_VALUE_IS_ONE);

            verify(goalInvitationRepository, times(1))
                    .save(goalInvitation);
            verify(userService, times(1))
                    .addGoalToUserGoals(goalInvitation.getInvited().getId(), goalInvitation.getGoal());
        }

        @Test
        @DisplayName("Обновляем сущность если цель существует")
        void When_GoalExists_Then_SuccessReject() {
            when(goalInvitationRepository.findById(LONG_POSITIVE_VALUE_IS_ONE))
                    .thenReturn(Optional.of(goalInvitation));

            goalInvitationService.rejectGoalInvitation(LONG_POSITIVE_VALUE_IS_ONE);

            verify(goalInvitationRepository, times(1))
                    .save(goalInvitation);
        }

        @Test
        @DisplayName("Успех если передаем null в фильтре")
        void When_FilterIsNull_Then_Success() {
            GoalInvitation anotherGoalInvitation = new GoalInvitation();

            User test = new User();
            test.setId(LONG_POSITIVE_VALUE_IS_TWO);

            User anotherTest = new User();
            anotherTest.setId(LONG_POSITIVE_VALUE_IS_ONE);

            Goal goal = new Goal();
            goal.setId(LONG_POSITIVE_VALUE_IS_TWO);

            anotherGoalInvitation.setId(LONG_POSITIVE_VALUE_IS_TWO);
            anotherGoalInvitation.setInviter(anotherTest);
            anotherGoalInvitation.setInvited(test);
            anotherGoalInvitation.setGoal(goal);

            List<GoalInvitation> goalInvitations = List.of(goalInvitation, anotherGoalInvitation);

            when(goalInvitationRepository.findAll())
                    .thenReturn(goalInvitations);

            goalInvitationService.getInvitations(null);

            verify(goalInvitationRepository, times(1))
                    .findAll();
            verify(goalInvitationMapper, times(1))
                    .toDto(goalInvitations);
        }

        @Test
        @DisplayName("Успех если передаем не null в фильтр")
        void When_FilterIsNotNull_Then_Success() {
            GoalInvitation anotherGoalInvitation = new GoalInvitation();

            User test = new User();
            test.setId(LONG_POSITIVE_VALUE_IS_TWO);

            User anotherTest = new User();
            anotherTest.setId(LONG_POSITIVE_VALUE_IS_ONE);

            Goal goal = new Goal();
            goal.setId(LONG_POSITIVE_VALUE_IS_TWO);

            anotherGoalInvitation.setId(LONG_POSITIVE_VALUE_IS_TWO);
            anotherGoalInvitation.setInviter(anotherTest);
            anotherGoalInvitation.setInvited(test);
            anotherGoalInvitation.setGoal(goal);

            List<GoalInvitation> goalInvitations = List.of(goalInvitation, anotherGoalInvitation);

            GoalInvitationFilterDto goalInvitationFilterDto = new GoalInvitationFilterDto();
            goalInvitationFilterDto.setInviterId(LONG_POSITIVE_VALUE_IS_TWO);

            when(goalInvitationRepository.findAll())
                    .thenReturn(goalInvitations);

            goalInvitationService.getInvitations(goalInvitationFilterDto);

            verify(goalInvitationRepository, times(1))
                    .findAll();
            verify(goalInvitationMapper, times(1))
                    .toDto(goalInvitations);
        }
    }

}