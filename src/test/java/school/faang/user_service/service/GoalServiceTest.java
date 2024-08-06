package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.GoalValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private List<GoalFilter> filters;
    @Mock
    private GoalValidator goalValidator;
    @Spy
    private GoalMapper goalMapper;

    @InjectMocks
    GoalService goalService;

    Long existUserId;
    Long notExistUserId;
    Long existGoalId;
    Long notExistGoalId;
    Skill skill1;
    Skill skill2;
    User user1;
    User user2;
    GoalDto goalDto;
    GoalDto goalWrongDto;
    GoalFilterDto filter;
    Goal goal;
    List<User> users;
    List<Skill> skills;

    @BeforeEach
    void setUp() {
        existUserId = 1L;
        notExistUserId = 2L;
        existGoalId = 1L;
        notExistGoalId = 2L;
        filter = GoalFilterDto.builder()
                .parentIdPattern(1L)
                .build();
        user1 = User.builder()
                .id(1)
                .username("test_user_1")
                .skills(skills)
                .build();
        user2 = User.builder()
                .id(2)
                .username("test_user_2")
                .skills(skills)
                .build();
        skill1 = Skill.builder()
                .id(1)
                .title("test_skill_1")
                .users(users)
                .build();
        skill2 = Skill.builder()
                .id(2)
                .title("test_skill_2")
                .users(users)
                .build();
        skills = List.of(skill1, skill2);
        users = List.of(user1, user2);
        goalDto = GoalDto.builder()
                .title("test_title")
                .description("test_description")
                .skillsToAchieveIds(List.of(1L, 2L))
                .userIds(List.of(1L, 2L))
                .build();
        goal = Goal.builder()
                .title("test_title")
                .description("test_description")
                .users(users)
                .skillsToAchieve(skills)
                .build();
        goalWrongDto = GoalDto.builder()
                .build();
    }

    @Test
    void testCreateGoalWithInvalidParametersThrowsException() {
        doThrow(DataValidationException.class).when(goalValidator).validateCreation(notExistUserId, goalDto);
        assertThrows(DataValidationException.class, () -> goalService.createGoal(notExistUserId, goalDto));
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void testCreateGoalWithValidParametersShouldSetStatusToGoal() {
        doNothing().when(goalValidator).validateCreation(existUserId, goalDto);
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.createGoal(existUserId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        Assertions.assertEquals(GoalStatus.ACTIVE, goal.getStatus());
    }

    @Test
    void testCreateGoalWithValidParametersAndUserIdsExistsInDtoShouldSetUsersToGoal() {
        doNothing().when(goalValidator).validateCreation(existUserId, goalDto);
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(userService.findAllById(goalDto.getUserIds())).thenReturn(users);
        goalService.createGoal(existUserId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        Assertions.assertTrue(goal.getUsers().containsAll(users));
    }

    @Test
    void testCreateGoalWithValidParametersShouldSetSkillsToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(skillService.findAllById(goalDto.getSkillsToAchieveIds())).thenReturn(skills);
        goalService.createGoal(existUserId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        Assertions.assertEquals(goal.getSkillsToAchieve(), skills);
    }

    @Test
    void testCreateGoalWithValidParametersShouldSaveGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.createGoal(existUserId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        verify(goalRepository).save(goal);
    }

    @Test
    void testCreateGoalWithValidParametersShouldReturnGoalDto() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.createGoal(existUserId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        verify(goalMapper).toDto(goalRepository.save(goal));
    }

    @Test
    void testUpdateGoalWithInvalidParametersShouldThrowsException() {
        doThrow(DataValidationException.class).when(goalValidator).validateUpdating(notExistGoalId, goalWrongDto);
        assertThrows(DataValidationException.class, () -> goalService.updateGoal(notExistGoalId, goalWrongDto));
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void testUpdateGoalWithValidParametersShouldSetIdToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.updateGoal(existGoalId, goalDto);
        Assertions.assertEquals(existGoalId, goal.getId());
    }

    @Test
    void testUpdateGoalWithValidParametersAndStatusCompletedShouldUpdateUsersAndSkills() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(existGoalId)).thenReturn(Optional.of(goal));
        when(skillService.findSkillsByGoalId(existGoalId)).thenReturn(skills);
        goalService.updateGoal(existGoalId, goalDto);
        verify(userService, times(users.size())).save(any(User.class));
        verify(skillService, times(4)).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void testUpdateGoalWithValidParametersAndUsersExistShouldSetUsersToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(userService.findAllById(goalDto.getUserIds())).thenReturn(users);
        goalService.updateGoal(existGoalId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        verify(goalMapper).toDto(goalRepository.save(goal));
        Assertions.assertEquals(users, goal.getUsers());
    }

    @Test
    void testUpdateGoalWithValidParametersShouldSetSkillsToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(skillService.findAllById(goalDto.getSkillsToAchieveIds())).thenReturn(skills);
        goal.setSkillsToAchieve(List.of());
        goalService.updateGoal(existGoalId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        Assertions.assertEquals(skills, goal.getSkillsToAchieve());
    }

    @Test
    void testUpdateGoalWithValidParametersShouldSaveGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.updateGoal(existGoalId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        verify(goalRepository).save(goal);
    }

    @Test
    void testUpdateGoalWithValidParametersShouldReturnGoalDto() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.updateGoal(existGoalId, goalDto);
        verify(goalMapper).toEntity(goalDto);
        verify(goalMapper).toDto(goalRepository.save(goal));
    }

    @Test
    void testDeleteGoalWithInvokesShouldDeleteById() {
        goalService.deleteGoal(existGoalId);
        verify(goalRepository).deleteById(existGoalId);
    }

    @Test
    void testFindSubtaskByGoalIdWithInvalidParametersShouldThrowsException() {
        doThrow(DataValidationException.class).when(goalValidator).validateGoalExistence(notExistGoalId);
        assertThrows(RuntimeException.class, () -> goalService.findSubtasksByGoalId(notExistGoalId, filter));
    }

    @Test
    void testFindSubtaskByGoalIdWithValidParametersShouldGetDtos() {
        goalService.findSubtasksByGoalId(existGoalId, filter);
        verify(goalRepository).findAll();
        verify(goalMapper).toDtos(any());
    }

    @Test
    void testFindGoalsByUserIdWithInvalidParametersShouldThrowsException() {
        doThrow(DataValidationException.class).when(goalValidator).validateUserExistence(notExistUserId);
        assertThrows(DataValidationException.class, () -> goalService.findGoalsByUserId(notExistUserId, filter));
    }

    @Test
    void testFindGoalsByUserIdWithValidParametersShouldGetDtos() {
        goalService.findGoalsByUserId(existUserId, filter);
        verify(goalRepository).findAll();
        verify(goalMapper).toDtos(any());
    }
}