package school.faang.user_service.service.controller.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.entity.filter.GoalFilters;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.SkillService;

import java.util.List;

public class GoalServiceTest {

    @InjectMocks
    GoalService goalService;

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private List<GoalFilters> goalFilters;

    @Test
    public void testCreateMaxNumbersGoalUser() {

    }

    @Test
    public void testCreateNormal() {

    }
}
