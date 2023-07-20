package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void testCreateGoal_Success() {
        Long userId = 1L;
        Goal goal = new Goal();

        ResponseEntity<String> response = goalController.createGoal(userId, goal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Goal created successfully", response.getBody());

        Mockito.verify(goalService).createGoal(userId, goal);
    }

    @Test
    public void testCreateGoal_InvalidData() {
        GoalService goalService = Mockito.mock(GoalService.class);

        Long userId = 1L;
        Goal goal = new Goal();
        doThrow(IllegalArgumentException.class)
                .when(goalService).createGoal(userId, goal);

        GoalController controller = new GoalController(goalService);

        ResponseEntity<String> response = controller.createGoal(userId, goal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expectedErrorMessage = "Invalid data";
        assertEquals(expectedErrorMessage, response.getBody());

    }

    @Test
    public void testCreateGoal_NullUserId() throws Exception {
        Goal goal = new Goal();
        goal.setTitle("Title");
        goal.setDescription("Description");

        doThrow(IllegalArgumentException.class)
                .when(goalService).createGoal(null, goal);

        ResponseEntity<String> response = goalController.createGoal(null, goal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data", response.getBody());
    }
}