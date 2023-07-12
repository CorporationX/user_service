package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalService goalService;

    @Test
    void getGoalsByUserTest() {
        Stream<Goal> goals = Stream.of(
                mock(Goal.class),
                mock(Goal.class)
        );
        GoalFilterDto filterDto = new GoalFilterDto();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(goals);

        List<GoalDto> goalDtos = goalService.getGoalsByUser(1L, filterDto);

        assertNotNull(goalDtos);
        assertEquals(2, goalDtos.size());

        verify(goalRepository).findGoalsByUserId(1L);
    }
}
