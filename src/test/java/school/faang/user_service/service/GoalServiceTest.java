package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalSkillFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;
    @Mock
    private UserService userService;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private List<GoalFilter> filters;
    @Mock
    private GoalValidator validator;
    private Goal goal;
    private GoalDto goalDto;
    private User user;
    private GoalFilterDto goalFilterDto;

    @BeforeEach
    void setUp() {
        List<Skill> skills = new ArrayList<>();

        user = User.builder().id(1L)
                .goals(new ArrayList<>())
                .skills(skills)
                .build();

        Goal parent = Goal.builder().id(2L).build();
        List<User> users = List.of(user);

        goal = Goal.builder()
                .id(1L)
                .parent(parent)
                .title("title")
                .description("description")
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(skills)
                .users(users)
                .build();

        goalDto = GoalDto.builder()
                .id(1L)
                .parentId(2L)
                .title("title")
                .description("description")
                .status(GoalStatus.ACTIVE)
                .skillIds(List.of(1L, 2L))
                .build();

        goalFilterDto = GoalFilterDto.builder().build();

        when(goalRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(goal));
        when(goalRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(parent));
        when(goalMapper.toDto(goal)).thenReturn(goalDto);
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(Stream.of(goal));
        when(goalRepository.findByParent(anyLong())).thenReturn(Stream.of(goal));
        when(userService.getUser(anyLong())).thenReturn(user);
        when(filters.stream()).thenReturn(Stream.of(
                new GoalTitleFilter(),
                new GoalSkillFilter(),
                new GoalSkillFilter()
        ));
    }

    @Test
    void getGoalById_shouldReturnGoalDto() {
        GoalDto actual = goalService.getGoalById(1L);
        verify(goalRepository).findById(1L);
        verify(goalMapper).toDto(goal);
        assertEquals(goalDto, actual);
    }

    @Test
    void getGoalsByUser_shouldInvokeRepositoryFindGoalsByUserId() {
        goalService.getGoalsByUser(1L, goalFilterDto);
        verify(goalRepository).findGoalsByUserId(1L);
    }

    @Test
    void getGoalsByUser_shouldInvokeMapperToDto() {
        goalService.getGoalsByUser(1L, goalFilterDto);
        verify(goalMapper).toDto(goal);
    }

    @Test
    void getGoalsByUser_shouldInvokeFiltersMethods() {
        goalService.getGoalsByUser(1L, goalFilterDto);
        filters.forEach(filter -> verify(filter).isApplicable(goalFilterDto));
        filters.forEach(filter -> verify(filter).apply(List.of(goalDto), goalFilterDto));
    }

    @Test
    void getGoalsByParent_shouldInvokeRepositoryFindByParent() {
        goalService.getGoalsByParent(2L, goalFilterDto);
        verify(goalRepository).findByParent(2L);
    }

    @Test
    void getGoalsByParent_shouldInvokeMapperToDto() {
        goalService.getGoalsByParent(2L, goalFilterDto);
        verify(goalMapper).toDto(goal);
    }

    @Test
    void getGoalsByParent_shouldInvokeFiltersMethods() {
        goalService.getGoalsByParent(2L, goalFilterDto);
        filters.forEach(filter -> verify(filter).isApplicable(goalFilterDto));
        filters.forEach(filter -> verify(filter).apply(List.of(goalDto), goalFilterDto));
    }

    @Test
    void create_shouldInvokeValidatorValidateToCreate() {
        goalService.create(1L, goalDto);
        verify(validator).validateToCreate(1L, goalDto);
    }

    @Test
    void create_shouldInvokeUserServiceGetUser() {
        goalService.create(1L, goalDto);
        verify(userService).getUser(1L);
    }

    @Test
    void create_shouldInvokeMapperToEntity() {
        goalService.create(1L, goalDto);
        verify(goalMapper).toEntity(goalDto);
    }

    @Test
    void create_shouldAddGoalToUser() {
        goalService.create(1L, goalDto);
        assertEquals(goal, user.getGoals().get(0));
    }

    @Test
    void create_shouldInvokeRepositorySave() {
        goalService.create(1L, goalDto);
        verify(goalRepository).save(goal);
    }

    @Test
    void update_shouldInvokeRepositoryFindById() {
        goalService.update(1L, goalDto);
        verify(goalRepository).findById(1L);
    }

    @Test
    void update_shouldInvokeValidatorValidateToUpdate() {
        goalService.update(1L, goalDto);
        verify(validator).validateToUpdate(goal, goalDto);
    }

    @Test
    void update_shouldUpdateGoalFields() {
        goal.setParent(null);
        goal.setTitle("Old title");
        goal.setDescription("Old description");
        goalDto.setStatus(GoalStatus.COMPLETED);
        goalDto.setSkillIds(null);

        goalService.update(1L, goalDto);
        assertAll(() -> {
            assertEquals(2L, goal.getParent().getId());
            assertEquals("title", goal.getTitle());
            assertEquals("description", goal.getDescription());
            assertEquals(GoalStatus.COMPLETED, goal.getStatus());
        });
    }

    @Test
    void update_shouldInvokeSkillRepositoryAssignSkillsToUsers() {
        goalDto.setStatus(GoalStatus.COMPLETED);
        when(skillService.getSKill(1L)).thenReturn(Skill.builder().id(1L).build());
        when(skillService.getSKill(2L)).thenReturn(Skill.builder().id(2L).build());

        goalService.update(1L, goalDto);
        verify(skillRepository).assignSkillToUser(1L, 1L);
        verify(skillRepository).assignSkillToUser(2L, 1L);
    }

    @Test
    void delete_shouldInvokeRepositoryFindById() {
        goalService.delete(1L);
        verify(goalRepository).findById(1L);
    }

    @Test
    void delete_shouldInvokeRepositoryDelete() {
        goalService.delete(1L);
        verify(goalRepository).delete(goal);
    }
}