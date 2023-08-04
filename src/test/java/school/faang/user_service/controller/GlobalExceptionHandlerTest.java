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

import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void testDataValidationException(){
        Mockito.when(skillService.create(Mockito.any(SkillDto.class))).thenThrow(
                new DataValidationException("IllegalArgument error"));

        assertThrows(DataValidationException.class, () -> {
            skillService.create(new SkillDto(null, "title"));
        });
    }

    @Test
    public void testEntityNotFoundException(){
        Long goalId = 1L;
        Mockito.doThrow(new EntityNotFoundException("Not found error"))
                .when(goalService).deleteGoal(goalId);

        assertThrows(EntityNotFoundException.class, () -> {
            goalService.deleteGoal(goalId);
        });
    }

    @Test
    public void testIllegalArgumentException() {
        Mockito.when(skillService.create(Mockito.any(SkillDto.class))).thenThrow(
                new IllegalArgumentException("IllegalArgument error"));

        assertThrows(IllegalArgumentException.class, () -> {
            skillService.create(new SkillDto(null, "title"));
        });
    }
}
