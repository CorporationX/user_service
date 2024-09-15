package school.faang.user_service.service.userService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private GoalRepository goalRepository;
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private User user;
//    private User user2;
//
//    private Goal goal3;
//    private List<Goal> userGoals;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setId(1L);
//        user.setActive(true);
//
//        user2 = new User();
//        user2.setId(2L);
//        user2.setActive(true);
//
//        Goal goal1 = new Goal();
//        goal1.setId(1L);
//        goal1.setUsers(new ArrayList<>(List.of(user)));
//        Goal goal2 = new Goal();
//        goal2.setId(2L);
//        goal2.setUsers(new ArrayList<>(List.of(user)));
//        goal3 = spy(new Goal());
//        goal3.setUsers(new ArrayList<>(List.of(user, user2)));
//        goal3.setId(3L);
//
//        userGoals = new ArrayList<>();
//        userGoals.add(goal1);
//        userGoals.add(goal2);
//        userGoals.add(goal3);
//        reset(goal3);
//    }
//
//    @Test
//    @DisplayName("Correct Deactivate")
//    void testCorrectDeactivateUser() {
//        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
//        when(goalRepository.findGoalsByUserId(user.getId())).thenReturn(userGoals.stream());
//
//        userService.deactivateUser(user.getId());
//
//        assertFalse(user.isActive());
//        verify(userRepository).save(user);
//        verify(goalRepository).deleteById(1L);
//        verify(goalRepository).deleteById(2L);
//
//        verify(goal3).setUsers(List.of(user2));
//    }
//
//    @Test
//    @DisplayName("User not found")
//    void testUserNotFound() {
//        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
//        assertThrows(NoSuchElementException.class, () -> userService.deactivateUser(user.getId()));
//    }
//
////    making sure that when no goals associate with user function doesnt throw exceptions or smth
//    @Test
//    @DisplayName("User with no goals")
//    void testNoGoals() {
//        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
//        when(goalRepository.findGoalsByUserId(user.getId())).thenReturn(new ArrayList<Goal>().stream());
//
//        userService.deactivateUser(user.getId());
//    }
//
////   Just in case someone changes code for "impossible?" case
//    @Test
//    @DisplayName("Goal with no users")
//    void testGoalWithNoUsers() {
//        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
//        Goal goalNoUsers = new Goal();
//        goalNoUsers.setId(4L);
//        when(goalRepository.findGoalsByUserId(user.getId())).thenReturn(new ArrayList<Goal>(List.of(goalNoUsers)).stream());
//
//        userService.deactivateUser(user.getId());
//    }
}