package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.dto.goal.GoalDto;
import school.faang.user_service.entity.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoalController.class)
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private GoalDto goalDto;
    private SearchGoalDto searchGoalDto;

    @BeforeEach
    void setUp() {
        goalDto = new GoalDto();
        goalDto.setId(1L);
        goalDto.setTitle("Learn Java");
        goalDto.setDescription("Learn Java basics");
        goalDto.setSkillIds(List.of(1L));
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setCreatedAt(LocalDateTime.of(2025, 3, 17, 10, 0));
        goalDto.setUpdatedAt(LocalDateTime.of(2025, 3, 17, 10, 0));

        searchGoalDto = new SearchGoalDto("Learn Java", GoalStatus.ACTIVE,
                LocalDateTime.of(2025, 3, 17, 10, 0));
    }

    @Test
    void createGoal_success_returnsCreated() throws Exception {
        Long userId = 1L;
        when(goalService.createGoal(userId, goalDto)).thenReturn(goalDto);

        mockMvc.perform(post("/goal")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Learn Java"))
                .andExpect(jsonPath("$.description").value("Learn Java basics"));

        verify(goalService).createGoal(userId, goalDto);
    }

    @Test
    void createGoal_serviceThrowsGoalDataException_returnsInternalServerError() throws Exception {
        Long userId = 1L;
        when(goalService.createGoal(userId, goalDto))
                .thenThrow(new GoalDataException("Too many active goals"));

        mockMvc.perform(post("/goal")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDto)))
                .andExpect(status().isInternalServerError());

        verify(goalService).createGoal(userId, goalDto);
    }

    @Test
    void updateGoal_success_returnsOk() throws Exception {
        Long goalId = 1L;
        when(goalService.updateGoal(goalId, goalDto)).thenReturn(goalDto);

        mockMvc.perform(put("/goal/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Learn Java"))
                .andExpect(jsonPath("$.description").value("Learn Java basics"));

        verify(goalService).updateGoal(goalId, goalDto);
    }

    @Test
    void updateGoal_serviceThrowsGoalDataException_returnsInternalServerError() throws Exception {
        Long goalId = 1L;
        when(goalService.updateGoal(goalId, goalDto))
                .thenThrow(new GoalDataException("Goal not found"));

        mockMvc.perform(put("/goal/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDto)))
                .andExpect(status().isInternalServerError());

        verify(goalService).updateGoal(goalId, goalDto);
    }

    @Test
    void deleteGoal_success_returnsNoContent() throws Exception {
        long goalId = 1L;
        doNothing().when(goalService).deleteGoal(goalId);

        mockMvc.perform(delete("/goal/{goalId}", goalId))
                .andExpect(status().isNoContent());

        verify(goalService).deleteGoal(goalId);
    }

    @Test
    void deleteGoal_serviceThrowsGoalDataException_returnsInternalServerError() throws Exception {
        long goalId = 1L;
        doThrow(new GoalDataException("Goal not found")).when(goalService).deleteGoal(goalId);

        mockMvc.perform(delete("/goal/{goalId}", goalId))
                .andExpect(status().isInternalServerError());

        verify(goalService).deleteGoal(goalId);
    }

    @Test
    void findSubtasksByGoalId_success_returnsOk() throws Exception {
        long goalId = 1L;
        List<GoalDto> subTasks = List.of(goalDto);
        when(goalService.findSubtasksByGoalId(goalId, searchGoalDto)).thenReturn(subTasks);

        mockMvc.perform(get("/goal/find-subtasks/{goalId}", goalId)
                        .param("title", searchGoalDto.title())
                        .param("status", searchGoalDto.status().toString())
                        .param("updatedAt", searchGoalDto.updatedAt().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Learn Java"));

        verify(goalService).findSubtasksByGoalId(goalId, searchGoalDto);
    }

    @Test
    void findSubtasksByGoalId_serviceThrowsGoalDataException_returnsInternalServerError() throws Exception {
        long goalId = 1L;
        when(goalService.findSubtasksByGoalId(goalId, searchGoalDto))
                .thenThrow(new GoalDataException("Subtasks not found"));

        mockMvc.perform(get("/goal/find-subtasks/{goalId}", goalId)
                        .param("title", searchGoalDto.title())
                        .param("status", searchGoalDto.status().toString())
                        .param("updatedAt", searchGoalDto.updatedAt().toString()))
                .andExpect(status().isInternalServerError());

        verify(goalService).findSubtasksByGoalId(goalId, searchGoalDto);
    }

    @Test
    void getGoalsByUser_success_returnsOk() throws Exception {
        long userId = 1L;
        List<GoalDto> goals = List.of(goalDto);
        when(goalService.getGoalsByUser(userId, searchGoalDto)).thenReturn(goals);

        mockMvc.perform(get("/goal/find-goals-by-user/{userId}", userId)
                        .param("title", searchGoalDto.title())
                        .param("status", searchGoalDto.status().toString())
                        .param("updatedAt", searchGoalDto.updatedAt().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Learn Java"));

        verify(goalService).getGoalsByUser(userId, searchGoalDto);
    }

    @Test
    void getGoalsByUser_serviceThrowsGoalDataException_returnsInternalServerError() throws Exception {
        long userId = 1L;
        when(goalService.getGoalsByUser(userId, searchGoalDto))
                .thenThrow(new GoalDataException("Goals not found"));

        mockMvc.perform(get("/goal/find-goals-by-user/{userId}", userId)
                        .param("title", searchGoalDto.title())
                        .param("status", searchGoalDto.status().toString())
                        .param("updatedAt", searchGoalDto.updatedAt().toString()))
                .andExpect(status().isInternalServerError());

        verify(goalService).getGoalsByUser(userId, searchGoalDto);
    }
}
