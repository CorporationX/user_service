package school.faang.user_service.service.user;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    GoalService goalService;

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Test: User exists")
    public void testFindUserByIdPositive() {
        Long userId = 1L;
        when(userService.isUserExist(userId)).thenReturn(true);
        assertTrue(userService.isUserExist(userId));
    }

    @Test
    @DisplayName("Test: User does not exists")
    public void testFindUserByIdNegative() {
        Long userId = 1L;
        when(userService.isUserExist(userId)).thenReturn(false);
        assertFalse(userService.isUserExist(userId));
    }

    @Test
    @DisplayName("Test: Should delete goals only with 1 user")
    public void testDeletingGoalsWithOneUser() {
        GoalDto runningDto = new GoalDto();
        runningDto.setUserIds(List.of(1L, 2L));

        GoalDto swimmingDto = new GoalDto();
        swimmingDto.setId(1L);
        swimmingDto.setUserIds(List.of(1L));

        GoalDto codingDto = new GoalDto();
        codingDto.setUserIds(List.of(1L, 2L));

        GoalDto paintingDto = new GoalDto();
        paintingDto.setId(2L);
        paintingDto.setUserIds(List.of(1L));


        List<GoalDto> goalDtos = List.of(runningDto, swimmingDto, codingDto, paintingDto);
        when(goalService.getGoalsByUser(1L)).thenReturn(goalDtos);

        userService.deactivateUser(1L);

        Mockito.verify(goalService, Mockito.times(1)).deleteAllByIds(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Test: Should remove user from goal if users > 1")
    public void testRemovingUserFromGoal() {
        GoalDto runningDto = new GoalDto();
        runningDto.setId(1L);
        runningDto.setUserIds(List.of(1L, 2L));

        GoalDto swimmingDto = new GoalDto();
        swimmingDto.setUserIds(List.of(1L));

        GoalDto codingDto = new GoalDto();
        codingDto.setId(2L);
        codingDto.setUserIds(List.of(1L, 2L));

        GoalDto paintingDto = new GoalDto();
        paintingDto.setUserIds(List.of(1L));


        List<GoalDto> goalDtos = List.of(runningDto, swimmingDto, codingDto, paintingDto);
        when(goalService.getGoalsByUser(1L)).thenReturn(goalDtos);

        userService.deactivateUser(1L);

        Mockito.verify(goalService, Mockito.times(1)).removeUserFromGoals(List.of(1L, 2L), 1L);
    }
}