package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GoalControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    private GoalController goalController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        goalController = new GoalController(goalService);
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
    }

    @Test
    public void testGetGoalsByUser_ValidInput() throws Exception {
        GoalDto goal1 = new GoalDto();
        goal1.setTitle("Goal 1");
        GoalDto goal2 = new GoalDto();
        goal2.setTitle("Goal 2");
        List<GoalDto> goals = Arrays.asList(goal1, goal2);
        when(goalService.getGoalsByUser(anyLong(), any())).thenReturn(goals);

        mockMvc.perform(MockMvcRequestBuilders.post("/goals/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Goal 1"))
                .andExpect(jsonPath("$[1].title").value("Goal 2"));

        verify(goalService, times(1)).getGoalsByUser(anyLong(), any());
    }

    @Test
    public void testUpdateGoal_ValidGoal() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Learn Java");
        goalDto.setDescription("Master Java programming language");

        when(goalService.updateGoal(anyLong(), any(GoalDto.class))).thenReturn(goalDto);

        long goalId = 1L;
        GoalDto result = goalController.updateGoal(goalId, goalDto);

        assertNotNull(result);
        assertEquals("Learn Java", result.getTitle());
        assertEquals("Master Java programming language", result.getDescription());
    }

    @Test
    public void testUpdateGoal_InvalidGoalTitle() {
        long goalId = 2L;
        GoalDto goalDto = new GoalDto();
        assertThrows(IllegalArgumentException.class, () -> goalController.updateGoal(goalId, goalDto));
    }

    @Test
    public void testUpdateGoal_CorrectServiceMethodInvocation() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Learn Kotlin");
        goalDto.setDescription("Explore Kotlin programming language");

        when(goalService.updateGoal(anyLong(), any(GoalDto.class))).thenReturn(goalDto);

        long goalId = 3L;
        GoalDto result = goalController.updateGoal(goalId, goalDto);

        verify(goalService, times(1)).updateGoal(goalId, goalDto);
    }
}
