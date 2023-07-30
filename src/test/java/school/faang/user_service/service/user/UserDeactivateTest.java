package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserDeactivateTest {
    private User user1;
    private User user2;
    private Goal goal;
    private UserDto userDto;
    @Mock
    GoalRepository goalRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "Sam", "sam@mail.ru");
        user1 = new User();
        user2 = new User();
        user1.setId(1);
        user2.setId(2);
        goal = new Goal();
        goal.setId(1L);
    }

    @Test
    void testDeleteGoalsPositive() {
        goal.setUsers(List.of(user1));
        List<Goal> goals = List.of(goal);
        user1.setGoals(goals);
        Mockito.when(userRepository.existsById(userDto.getId())).thenReturn(true);
        Mockito.when(goalRepository.findGoalsByUserId(userDto.getId())).thenReturn(goals.stream());
        userService.deactivateUser(userDto);
        Mockito.verify((goalRepository), Mockito.times(1)).deleteById(userDto.getId());
    }

    @Test
    void testDeleteGoalsNegative() {
        goal.setUsers(List.of(user1, user2));
        List<Goal> goals = List.of(goal);
        user1.setGoals(goals);
        user2.setGoals(goals);
        Mockito.when(userRepository.existsById(userDto.getId())).thenReturn(true);
        Mockito.when(goalRepository.findGoalsByUserId(userDto.getId())).thenReturn(goals.stream());
        userService.deactivateUser(userDto);
        Mockito.verify((goalRepository), Mockito.times(0)).deleteById(userDto.getId());
    }
}
