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
    public void testDeleteGoal_ExistingGoal() {
        long goalId = 1L;

        goalController.deleteGoal(goalId);

        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @Test
    public void testDeleteGoal_NonExistentGoal_NoExceptionThrown() {
        long nonExistentGoalId = 10L;

        doNothing().when(goalService).deleteGoal(anyLong());

        goalController.deleteGoal(nonExistentGoalId);

        verify(goalService, times(1)).deleteGoal(nonExistentGoalId);
    }
}