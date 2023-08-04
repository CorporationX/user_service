package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.goal.GoalService;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillService skillService;

    @MockBean
    private GoalService goalService;

    @Test
    public void testDataValidationException() throws Exception {
        Mockito.when(skillService.create(Mockito.any(SkillDto.class))).thenThrow(
                new DataValidationException("Validation error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/skills")
                        .param("title", "title")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation error"));
    }

    @Test
    public void testEntityNotFoundException() throws Exception {
        Long goalId = 1L;
        Mockito.doThrow(new EntityNotFoundException("Not found error"))
                .when(goalService).deleteGoal(goalId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/goals/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Not found error"));
    }

    @Test
    public void testIllegalArgumentException() throws Exception {
        Mockito.when(skillService.create(Mockito.any(SkillDto.class))).thenThrow(
                new IllegalArgumentException("IllegalArgument error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/skills")
                        .param("title", "title")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("IllegalArgument error"));
    }
}
