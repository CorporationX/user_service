package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalDescriptionFilter;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalSkillFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private UserService userService;

    @Mock
    private GoalTitleFilter goalTitleFilter;

    @Mock
    private GoalDescriptionFilter goalDescriptionFilter;

    @Mock
    private GoalStatusFilter goalStatusFilter;

    @Mock
    private GoalSkillFilter goalSkillFilter;

    @Mock
    private List<GoalFilter> goalFilters;

    @Mock
    private GoalValidator goalValidator;

    @Spy
    private GoalMapperImpl goalMapper;

    @Captor
    private ArgumentCaptor<Goal> captor;

    private User user;
    private Skill skillOne;
    private Skill skillTwo;
    private Skill skillThree;
    private List<Skill> skills;
    private List<Long> skillIds;
    private GoalDto goalDto;
    private Goal parent;

    @BeforeEach
    public void setUp() {
        user = User.builder().id(1L).skills(new ArrayList<>()).build();

        skillOne = Skill.builder().id(1L).build();
        skillTwo = Skill.builder().id(2L).build();
        skillThree = Skill.builder().id(3L).build();
        skills = List.of(skillOne, skillTwo, skillThree);
        skillIds = List.of(1L, 2L, 3L);
        parent = Goal.builder().id(1L).build();
        goalDto = GoalDto.builder()
                .title("title")
                .description("desc")
                .parentId(parent.getId())
                .status(GoalStatus.ACTIVE)
                .skillIds(skillIds)
                .build();
    }

    @Test
    public void testCreateGoalSaves() {
        long userId = user.getId();
        Goal expected = goalMapper.toEntity(goalDto);
        when(skillService.getSkillById(1L)).thenReturn(skillOne);
        when(skillService.getSkillById(2L)).thenReturn(skillTwo);
        when(skillService.getSkillById(3L)).thenReturn(skillThree);
        expected.setSkillsToAchieve(skills);
        when(userService.getById(userId)).thenReturn(user);
        expected.setUsers(new ArrayList<>());
        expected.getUsers().add(user);

        goalService.createGoal(userId, goalDto);

        verify(goalRepository, times(1)).save(captor.capture());
        assertEquals(expected, captor.getValue());
        verify(goalMapper, times(1)).toDto(captor.getValue());
    }

    @Test
    public void testUpdateGoalNotFound() {
        when(goalRepository.findById(1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(1L, goalDto));
    }

    @Test
    public void testUpdateGoalUpdated() {
        Goal fromRepository = Goal.builder()
                .id(10L)
                .title("old title")
                .description("old desc")
                .skillsToAchieve(List.of(new Skill()))
                .status(GoalStatus.ACTIVE)
                .parent(new Goal())
                .users(List.of(user))
                .build();
        when(goalRepository.findById(fromRepository.getId())).thenReturn(Optional.of(fromRepository));
        when(goalRepository.findById(goalDto.getParentId())).thenReturn(Optional.of(parent));
        when(skillService.getSkillById(1L)).thenReturn(skillOne);
        when(skillService.getSkillById(2L)).thenReturn(skillTwo);
        when(skillService.getSkillById(3L)).thenReturn(skillThree);

        Goal expected = goalMapper.toEntity(goalDto);
        expected.setParent(parent);
        expected.setSkillsToAchieve(skills);
        expected.setId(10L);
        expected.setUsers(List.of(user));

        goalService.updateGoal(fromRepository.getId(), goalDto);


        verify(goalMapper, times(1)).toDto(captor.capture());
        assertEquals(expected, captor.getValue());
    }

    @Test
    public void findSubtasksByGoalId() {
        Goal goalOne = Goal.builder().title("any title").build();
        Goal goalTwo = Goal.builder().title("title").description("desc").build();
        Goal goalThree = Goal.builder().title("no").build();

        List<GoalDto> goals = Stream.of(goalOne, goalTwo, goalThree)
                .map(goalMapper::toDto).toList();

        GoalFilterDto filter = GoalFilterDto.builder()
                .titlePattern("title")
                .descriptionPattern("desc")
                .build();

        when(goalRepository.findByParent(1L)).thenReturn(Stream.of(goalOne, goalTwo, goalThree));
        when(goalFilters.stream()).thenReturn(getFilterStream());
        when(goalTitleFilter.isApplicable(filter)).thenReturn(true);
        when(goalDescriptionFilter.isApplicable(filter)).thenReturn(true);
        when(goalStatusFilter.isApplicable(filter)).thenReturn(false);
        when(goalSkillFilter.isApplicable(filter)).thenReturn(false);

        goalService.findSubtasksByGoalId(1L, filter);

        verify(goalTitleFilter, times(1)).apply(goals, filter);
        verify(goalDescriptionFilter, times(1)).apply(goals, filter);
        verify(goalStatusFilter, times(0)).apply(goals, filter);
        verify(goalSkillFilter, times(0)).apply(goals, filter);
    }

    @Test
    public void findGoalsByUserId() {
        Goal goalOne = new Goal();
        Goal goalTwo = new Goal();
        Goal goalThree = new Goal();
        List<GoalDto> goals = Stream.of(goalOne, goalTwo, goalThree).map(goalMapper::toDto).toList();
        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goalOne, goalTwo, goalThree));
        when(goalFilters.stream()).thenReturn(getFilterStream());
        GoalFilterDto filter = new GoalFilterDto();

        when(goalTitleFilter.isApplicable(filter)).thenReturn(true);
        when(goalDescriptionFilter.isApplicable(filter)).thenReturn(true);
        when(goalStatusFilter.isApplicable(filter)).thenReturn(true);
        when(goalSkillFilter.isApplicable(filter)).thenReturn(false);

        goalService.getGoalsByUserId(1L, filter);

        verify(goalTitleFilter, times(1)).apply(goals, filter);
        verify(goalDescriptionFilter, times(1)).apply(goals, filter);
        verify(goalStatusFilter, times(1)).apply(goals, filter);
        verify(goalSkillFilter, times(0)).apply(goals, filter);
    }

    private Stream<GoalFilter> getFilterStream() {
        return Stream.of(goalTitleFilter, goalDescriptionFilter,
                goalStatusFilter, goalSkillFilter);
    }
}
