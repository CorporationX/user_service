package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.EntityNotFoundException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.validation.goal.GoalValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GoalServiceTest {
    @Autowired
    private GoalRepository goalRepository;
    private GoalMapper goalMapper;
    private SkillRepository skillRepository;
    private GoalValidation goalValidation;
    private GoalFilter goalFilter;
    private List<GoalFilter> goalFilters;
    private UserRepository userRepository;
    private GoalService goalService;


    @BeforeEach
    void setUp() {
        goalRepository = mock(GoalRepository.class);
        goalMapper = mock(GoalMapper.class);
        skillRepository = mock(SkillRepository.class);
        goalValidation = mock(GoalValidation.class);
        goalFilter = mock(GoalFilter.class);
        userRepository = mock(UserRepository.class);
        goalFilters = List.of(goalFilter);
        goalService = new GoalService(goalRepository, goalMapper, skillRepository, goalValidation, userRepository, goalFilters);
    }

    @Test
    void testCreateGoal_UserDoesntExist() {
        Long userId = 1L;
        GoalDto goalDto = getGoalDto();
        Goal goalCreated = getGoal();

        when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId()))
                .thenReturn(goalCreated);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    void testCreateGoalSaveRepository() {
        Long userId = 1L;
        GoalDto goalDto = getGoalDto();
        List<Long> skillsId = List.of(1L, 2L);
        goalDto.setSkillIds(skillsId);
        Goal goalCreated = getGoal();
        User user = new User();
        List<Skill> skills = getSkills();

        when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId()))
                .thenReturn(goalCreated);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.save(goalCreated)).thenReturn(goalCreated);
        when(goalMapper.toDto(goalCreated)).thenReturn(goalDto);
        when(skillRepository.findAllById(skillsId)).thenReturn(skills);

        GoalDto goalDtoCreated = goalService.createGoal(userId, goalDto);

        verify(goalValidation, times(1))
                .validateGoalCreate(goalDto.getId(), goalDto, 3);
        verify(goalRepository, times(1))
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        verify(skillRepository, times(1))
                .findAllById(goalDto.getSkillIds());
        verify(userRepository, times(1)).findById(userId);

        assertEquals(goalDto, goalDtoCreated);
    }

    @Test
    void testUpdateGoalSaveRepository() {
        Long goalId = 1L;
        GoalDto goalDto = getGoalDto();
        Goal goalCreated = getGoal();

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalCreated));
        when(goalRepository.save(goalCreated)).thenReturn(goalCreated);
        when(goalMapper.toDto(goalCreated)).thenReturn(goalDto);

        GoalDto goalDtoCreated = goalService.updateGoal(goalId, goalDto);

        verify(goalValidation, times(1)).validateGoalUpdate(goalId, goalDto);
        verify(goalValidation, times(1)).validateExistGoal(goalId);

        assertEquals(goalDto, goalDtoCreated);
    }

    @Test
    void testDeleteGoalFromRepository() {
        long goalId = 1L;

        assertDoesNotThrow(() -> goalService.deleteGoal(goalId));

        verify(goalValidation, times(1)).validateExistGoal(goalId);
        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    void test_findSubtasksByGoalId_GoalIdIsValid() {
        long goalId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        Stream<Goal> goals = getGoals();

        when(goalRepository.findByParent(goalId)).thenReturn(goals);
        when(goalFilter.isApplicable(filters)).thenReturn(true);

        goalService.findSubtasksByGoalId(goalId, filters);

        verify(goalValidation, times(1))
                .validateExistGoal(goalId);
        verify(goalRepository, times(1)).findByParent(goalId);
        verify(goalFilter).isApplicable(filters);
        verify(goalFilter).apply(anyList(), any(GoalFilterDto.class));
        verify(goalMapper).toDto(anyList());
    }

    @Test
    void test_findGoalsByUserId_GoalIdIsValid_DoesNotThrows() {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        Stream<Goal> goals = getGoals();

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals);
        when(goalFilter.isApplicable(filters)).thenReturn(true);

        goalService.findGoalsByUserId(userId, filters);

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
        verify(goalFilter).isApplicable(filters);
        verify(goalFilter).apply(anyList(), any(GoalFilterDto.class));
    }

    private GoalDto getGoalDto() {
        return GoalDto.builder()
                .id(1L)
                .description("description")
                .parentId(1L)
                .title("title")
                .status(GoalStatus.ACTIVE)
                .skillIds(List.of(1L, 2L, 3L)).build();
    }

    private Goal getGoal() {
        return Goal.builder()
                .id(1L)
                .parent(new Goal())
                .description("description")
                .title("title")
                .status(GoalStatus.ACTIVE)
                .users(List.of(new User()))
                .skillsToAchieve(List.of(new Skill()))
                .build();
    }

    private Stream<Goal> getGoals() {
        return Stream.of(
                Goal.builder().id(1L).status(GoalStatus.ACTIVE).title("Title1").build(),
                Goal.builder().id(2L).status(GoalStatus.ACTIVE).title("Title2").build(),
                Goal.builder().id(3L).status(GoalStatus.ACTIVE).title("Title3").build()
        );
    }

    private List<Skill> getSkills(){
        return new ArrayList<>(List.of(Skill.builder().id(1L).build(),
                Skill.builder().id(2L).build()));
    }
}
