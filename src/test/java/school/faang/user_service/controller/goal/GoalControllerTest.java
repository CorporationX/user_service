package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validation.GoalValidator;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetGoalsByUser() throws Exception {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        List<GoalDto> mockGoals = Collections.singletonList(new GoalDto());
        when(goalService.getGoalsByUser(eq(userId), any(GoalFilterDto.class))).thenReturn(mockGoals);

        mockMvc.perform(get("/goals/users/{userId}/goals", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filters)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testCreateGoal() throws Exception {
        Long userId = 1L;
        GoalDto mockGoal = new GoalDto();
        mockGoal.setTitle("Test Goal");

        when(goalService.createGoal(eq(userId), any(GoalDto.class))).thenReturn(mockGoal);

        mockMvc.perform(post("/goals/users/{userId}/goals", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockGoal)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Goal"));
    }

    @Test
    public void testFindSubtasksByGoalId() throws Exception {
        Long goalId = 1L;
        GoalFilterDto filterDto = new GoalFilterDto();
        List<GoalDto> mockSubtasks = Arrays.asList(new GoalDto(), new GoalDto());

        when(goalService.findSubtasksByGoalId(anyLong(), any(GoalFilterDto.class)))
                .thenReturn(mockSubtasks);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();

        mockMvc.perform(post("/goals/subtasks/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));

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
