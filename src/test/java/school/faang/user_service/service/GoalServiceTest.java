package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private GoalMapper goalMapper;
    @Mock
    private GoalValidator goalValidator;
    @Mock
    private List<GoalFilter> filters;

    @InjectMocks
    GoalService goalService;

    Long existUserId;
    Long notExistUserId;
    Long existGoalId;
    Long notExistGoalId;
    Skill skill_1;
    Skill skill_2;
    User user_1;
    User user_2;
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
        user_1 = User.builder()
                .id(1)
                .username("test_user_1")
                .skills(skills)
                .build();
        user_2 = User.builder()
                .id(2)
                .username("test_user_2")
                .skills(skills)
                .build();
        skill_1 = Skill.builder()
                .id(1)
                .title("test_skill_1")
                .users(users)
                .build();
        skill_2 = Skill.builder()
                .id(2)
                .title("test_skill_2")
                .users(users)
                .build();
        skills = List.of(skill_1, skill_2);
        users = List.of(user_1, user_2);
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
    void createGoal_whenInvalidParameters_thenThrowsException() {
        doThrow(DataValidationException.class).when(goalValidator).validateCreation(notExistUserId, goalWrongDto);
        assertThrows(DataValidationException.class, () -> goalService.createGoal(notExistUserId, goalWrongDto));
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void createGoal_whenValidParameters_thenSetStatusToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.createGoal(existUserId, goalDto);
        Assertions.assertEquals(GoalStatus.ACTIVE, goal.getStatus());
    }

    @Test
    void createGoal_whenValidParametersAndUserIdsExistsInDto_thenSetUsersToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(userService.findAllById(goalDto.getUserIds())).thenReturn(users);
        goalService.createGoal(existUserId, goalDto);
        Assertions.assertTrue(goal.getUsers().containsAll(users));
    }

    @Test
    void createGoal_whenValidParameters_thenSetSkillsToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(skillService.findAllById(goalDto.getSkillsToAchieveIds())).thenReturn(skills);
        goalService.createGoal(existUserId, goalDto);
        Assertions.assertEquals(goal.getSkillsToAchieve(), skills);
    }

    @Test
    void createGoal_whenValidParameters_thenSaveGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.createGoal(existUserId, goalDto);
        verify(goalRepository).save(goal);
    }

    @Test
    void createGoal_whenValidParameters_thenReturnGoalDto() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.createGoal(existUserId, goalDto);
        verify(goalMapper).toDto(goalRepository.save(goal));
    }

    @Test
    void updateGoal_whenInvalidParameters_thenThrowsException() {
        doThrow(DataValidationException.class).when(goalValidator).validateUpdating(notExistGoalId, goalWrongDto);
        assertThrows(DataValidationException.class, () -> goalService.updateGoal(notExistGoalId, goalWrongDto));
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void updateGoal_whenValidParameters_thenSetIdToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.updateGoal(existGoalId, goalDto);
        Assertions.assertEquals(existGoalId, goal.getId());
    }

    @Test
    void updateGoal_whenValidParametersAndStatusCompleted_thenUpdateUsersAndSkills() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(existGoalId)).thenReturn(Optional.of(goal));
        when(skillService.findSkillsByGoalId(existGoalId)).thenReturn(skills);
        goalService.updateGoal(existGoalId, goalDto);
        verify(userService, times(users.size())).save(any(User.class));
        verify(skillService, times(4)).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void updateGoal_whenValidParametersAndUsersExist_thenSetUsersToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(userService.findAllById(goalDto.getUserIds())).thenReturn(users);
        goalService.updateGoal(existGoalId, goalDto);
        Assertions.assertEquals(users, goal.getUsers());
    }

    @Test
    void updateGoal_whenValidParameters_thenSetSkillsToGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(skillService.findAllById(goalDto.getSkillsToAchieveIds())).thenReturn(skills);
        goal.setSkillsToAchieve(List.of());
        goalService.updateGoal(existGoalId, goalDto);
        Assertions.assertEquals(skills, goal.getSkillsToAchieve());
    }

    @Test
    void updateGoal_whenValidParameters_thenSaveGoal() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.updateGoal(existGoalId, goalDto);
        verify(goalRepository).save(goal);
    }

    @Test
    void updateGoal_whenValidParameters_thenReturnGoalDto() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        goalService.updateGoal(existGoalId, goalDto);
        verify(goalMapper).toDto(goalRepository.save(goal));
    }

    @Test
    void deleteGoal_whenInvokes_thenDeleteById() {
        goalService.deleteGoal(existGoalId);
        verify(goalRepository).deleteById(existGoalId);
    }

    @Test
    void findSubtaskByGoalId_whenInvalidParameters_thenThrowsException() {
        doThrow(DataValidationException.class).when(goalValidator).validateGoalExistence(notExistGoalId);
        assertThrows(DataValidationException.class, () -> goalService.findSubtasksByGoalId(notExistGoalId, filter));
    }

    @Test
    void findSubtaskByGoalId_whenValidParameters_thenGetDtos() {
        goalService.findSubtasksByGoalId(existGoalId, filter);
        verify(goalMapper).toDtos(any());
    }

    @Test
    void findGoalsByUserId_whenInvalidParameters_thenThrowsException() {
        doThrow(DataValidationException.class).when(goalValidator).validateUserExistence(notExistUserId);
        assertThrows(DataValidationException.class, () -> goalService.findGoalsByUserId(notExistUserId, filter));
    }

    @Test
    void findGoalsByUserId_whenValidParameters_thenGetDtos() {
        goalService.findGoalsByUserId(existUserId, filter);
        verify(goalMapper).toDtos(any());
    }
}