package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mappers.GoalDtoMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalDtoMapper goalDtoMapper;
    @Mock
    private Goal goal;

    @InjectMocks
    GoalService goalService;

    Long existUserId;
    Long notExistUserId;
    Long existGoalId;
    Long notExistGoalId;
    List<Long> skillIds;
    GoalDto goalDto;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        goalDto = goalDtoMapper.toDto(goal);
        existUserId = 1L;
        notExistUserId = 2L;
        existGoalId = 1L;
        notExistGoalId = 2L;
        skillIds = List.of(1L, 2L);
//        when(userRepository.findById(existUserId)).thenReturn(Optional.of(User.builder().id(existUserId).build()));
//        when(userRepository.findById(notExistUserId)).thenThrow(new RuntimeException());
//        when(goalRepository.findById(existGoalId)).thenReturn(Optional.of(Goal.builder().id(existGoalId).build()));
//        when(goalRepository.findById(notExistGoalId)).thenThrow(new RuntimeException());
//        when(goal.getStatus()).thenReturn(GoalStatus.ACTIVE);


    }

    @Test
    void test_1() {
//        when(userRepository.findById(notExistUserId)).thenThrow(new RuntimeException());
//        assertThrows(RuntimeException.class, () -> goalService.createGoal(notExistUserId, goalDto));
    }

    @Test
    void test_2() {
//        when(userRepository.findById(existUserId)).thenReturn(Optional.of(user));
//        when(goalRepository.findById(notExistGoalId)).thenThrow(new RuntimeException());
//        assertThrows(RuntimeException.class, () -> goalService.createGoal(existUserId, goalDto));
//        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void test_3() {
//        int negativeAnswer = 4;
//        int positiveAnswer = 2;
//        when(userRepository.findById(existUserId)).thenReturn(Optional.of(user));
//        when(goalRepository.findById(existGoalId)).thenReturn(Optional.of(goal));
//        when(goalRepository.countActiveGoalsPerUser(existUserId)).thenReturn(negativeAnswer);
//        assertThrows(RuntimeException.class, () -> goalService.createGoal(existUserId, goalDto));
//        verify(goalRepository).countActiveGoalsPerUser(existUserId);
    }

    @Test
    void createGoal() {

    }

    @Test
    void updateGoal() {
    }

    @Test
    void deleteGoal() {
    }

    @Test
    void findSubtaskByGoalId() {
    }

    @Test
    void findGoalsByUserId() {
    }
}