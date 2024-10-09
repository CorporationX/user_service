package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.CommonIntegrationTest;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static school.faang.user_service.CommonFactory.GOAL_DESCRIPTION;
import static school.faang.user_service.CommonFactory.GOAL_TITLE;
import static school.faang.user_service.factory.GoalFactory.buildDefaultCreateGoalDto;
import static school.faang.user_service.factory.GoalFactory.buildDefaultUpdateGoalDto;

public class GoalControllerTest extends CommonIntegrationTest {
    @Test
    void testCreateGoal_Success_DefaultCreateGoalDto() throws Exception {
        /* Create User */
        User savedUser = saveDefaultUser();

        /* Create Goal */
        CreateGoalDto createGoalDto = buildDefaultCreateGoalDto(savedUser.getId());
        mockMvc.perform(post("/goals")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGoalDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.parentGoalId").isEmpty())
                .andExpect(jsonPath("$.title").value(GOAL_TITLE))
                .andExpect(jsonPath("$.description").value(GOAL_DESCRIPTION))
                .andExpect(jsonPath("$.status").value(GoalStatus.ACTIVE.name()))
                .andExpect(jsonPath("$.userIds").isArray())
                .andExpect(jsonPath("$.userIds", hasSize(1)))
                .andExpect(jsonPath("$.userIds[0]", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.skillIds").isEmpty());

        /* Check created Goal in DB */
        List<Goal> goalsAfterCreation = goalRepository.findAll();
        assertEquals(1, goalsAfterCreation.size());
        assertEquals(createGoalDto.getTitle(), goalsAfterCreation.get(0).getTitle());
        assertEquals(createGoalDto.getDescription(), goalsAfterCreation.get(0).getDescription());
    }

    @Test
    void testUpdateGoal_Success_DefaultCreateGoalDto() throws Exception {
        /* Create Goal before updating */
        GoalResponseDto responseDto = createDefaultGoal();
        Long createdGoalId = responseDto.getId();

        /* Update Goal */
        UpdateGoalDto updateGoalDto = buildDefaultUpdateGoalDto(createdGoalId);
        mockMvc.perform(patch("/goals")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGoalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updateGoalDto.getGoalId()))
                .andExpect(jsonPath("$.parentGoalId").isEmpty())
                .andExpect(jsonPath("$.title").value(updateGoalDto.getTitle()))
                .andExpect(jsonPath("$.description").value(updateGoalDto.getDescription()))
                .andExpect(jsonPath("$.status").value(GoalStatus.ACTIVE.name()))
                .andExpect(jsonPath("$.userIds").isArray())
                .andExpect(jsonPath("$.userIds", hasSize(1)))
                .andExpect(jsonPath("$.skillIds").isEmpty());

        /* Check updated Goal in DB */
        List<Goal> goalsAfterUpdating = goalRepository.findAll();
        assertEquals(1, goalsAfterUpdating.size());
        assertEquals(updateGoalDto.getGoalId(), goalsAfterUpdating.get(0).getId());
        assertEquals(updateGoalDto.getTitle(), goalsAfterUpdating.get(0).getTitle());
        assertEquals(updateGoalDto.getDescription(), goalsAfterUpdating.get(0).getDescription());
    }

    @Test
    void testDeleteGoal_Success() throws Exception {
        /* Create Goal before deleting */
        GoalResponseDto responseDto = createDefaultGoal();
        Long createdGoalId = responseDto.getId();

        /* Delete Goal */
        UpdateGoalDto updateGoalDto = buildDefaultUpdateGoalDto(createdGoalId);
        mockMvc.perform(delete("/goals/{goalId}", createdGoalId))
                .andExpect(status().isOk());

        /* Check if Goal was deleted in DB */
        List<Goal> goalsAfterUpdating = goalRepository.findAll();
        assertEquals(0, goalsAfterUpdating.size());
    }
}
