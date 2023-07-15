package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Spy
    private GoalMapperImpl goalMapper;

    private final List<GoalFilter> goalFilters = List.of(new GoalStatusFilter(), new GoalTitleFilter());

    private GoalService goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        goalService = new GoalService(goalRepository, goalMapper, goalFilters);

        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("title1");
        goal1.setStatus(GoalStatus.ACTIVE);

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("title2");
        goal2.setStatus(GoalStatus.COMPLETED);

        Stream<Goal> goalStream = Stream.<Goal>builder().add(goal1).add(goal2).build();

        Mockito.when(goalRepository.findGoalsByUserId(Mockito.anyLong()))
                .thenReturn(goalStream);
    }

    @Test
    public void testGetGoalsByUserIdInvalidLessThanOne() {
        assertThrows(DataValidationException.class,
                () -> goalService.getGoalsByUser(0L, new GoalFilterDto()), "User id cannot be less than 1!");
    }

    @Test
    public void testGetGoalsByUserIdInvalidNull() {
        assertThrows(DataValidationException.class,
                () -> goalService.getGoalsByUser(null, new GoalFilterDto()), "User id cannot be null!");
    }

    @Test
    public void testGetGoalsByUserIdValidNoneFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, null);
        List<GoalDto> expected = List.of(
                new GoalDto(1L, null, null, "title1", GoalStatus.ACTIVE, null),
                new GoalDto(2L, null, null, "title2", GoalStatus.COMPLETED, null)
        );

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testGetGoalsByUserIdValidStatusFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, new GoalFilterDto(null, GoalStatus.ACTIVE));
        List<GoalDto> expected = List.of(
                new GoalDto(1L, null, null, "title1", GoalStatus.ACTIVE, null)
        );

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testGetGoalsByUserIdValidTitleFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, new GoalFilterDto("1", null));
        List<GoalDto> expected = List.of(
                new GoalDto(1L, null, null, "title1", GoalStatus.ACTIVE, null)
        );

        assertIterableEquals(expected, goalDtoList);
    }
}