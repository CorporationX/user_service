package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class GoalControllerTest {

    @Mock
    private GoalService goalService;

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalController goalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGoal_ShouldReturnCreatedGoal() {
        GoalDto goalDto = new GoalDto();
        Goal goal = new Goal();
        when(goalService.createGoal(anyLong(), anyString(), anyString(), anyLong(), anyList())).thenReturn(goal);
        when(goalMapper.toDto(any())).thenReturn(goalDto);

        ResponseEntity<GoalDto> response = goalController.createGoal(1L, goalDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(goalDto, response.getBody());
    }

    @Test
    void createGoal_ShouldThrowException_WhenServiceThrowsException() {
        GoalDto goalDto = new GoalDto();
        when(goalService.createGoal(any(), any(), any(), any(), any())).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> goalController.createGoal(1L, goalDto));
    }

    @Test
    void updateGoal_ShouldReturnUpdatedGoal() {
        GoalDto goalDto = new GoalDto();
        Goal goal = new Goal();
        when(goalService.updateGoal(anyLong(), any(Goal.class), anyList())).thenReturn(goal);
        when(goalMapper.toDto(any())).thenReturn(goalDto);
        when(goalMapper.toEntity(any())).thenReturn(goal);

        ResponseEntity<GoalDto> response = goalController.updateGoal(1L, goalDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(goalDto, response.getBody());
    }

    @Test
    void updateGoal_ShouldThrowException_WhenServiceThrowsException() {
        GoalDto goalDto = new GoalDto();
        when(goalService.updateGoal(any(), any(), any())).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> goalController.updateGoal(1L, goalDto));
    }

    @Test
    void deleteGoal_ShouldReturnSuccessMessage() {
        doNothing().when(goalService).deleteGoal(anyLong());

        ResponseEntity<String> response = goalController.deleteGoal(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Goal with id 1 has been deleted successfully !", response.getBody());
    }

    @Test
    void findSubGoalsByParentIdWithFilter_ShouldReturnSubGoals() {
        GoalFilterDto filterDto = new GoalFilterDto();
        GoalDto goalDto = new GoalDto();
        List<GoalDto> goalDtoList = Collections.singletonList(goalDto);
        when(goalService.findSubGoalsByParentId(anyLong(), any(GoalFilterDto.class))).thenReturn(Collections.singletonList(new Goal()));
        when(goalMapper.toDtoList(any(List.class))).thenReturn(goalDtoList);

        ResponseEntity<List<GoalDto>> response = goalController.findSubGoalsByParentIdWithFilter(1L, filterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(goalDtoList, response.getBody());
    }

    @Test
    void findGoalsByUserIdWithFilter_ShouldReturnGoals() {
        GoalFilterDto filterDto = new GoalFilterDto();
        GoalDto goalDto = new GoalDto();
        List<GoalDto> goalDtoList = Collections.singletonList(goalDto);
        when(goalService.findSubGoalsByUserId(anyLong(), any(GoalFilterDto.class))).thenReturn(Collections.singletonList(new Goal()));
        when(goalMapper.toDtoList(any(List.class))).thenReturn(goalDtoList);

        ResponseEntity<List<GoalDto>> response = goalController.findGoalsByUserIdWithFilter(1L, filterDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(goalDtoList, response.getBody());
    }
}