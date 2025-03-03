package school.faang.user_service.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkillController.class)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillService skillService;

    @Autowired
    private ObjectMapper objectMapper;

    private SkillDto validSkillDto;

    @BeforeEach
    void setUp() {
        validSkillDto = new SkillDto();
        validSkillDto.setTitle("Java");
    }

    @Test
    void testCreateSkill_Success() throws Exception {

        when(skillService.create(any(SkillDto.class))).thenReturn(validSkillDto);

        mockMvc.perform(post("/api/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSkillDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java"));

        verify(skillService, times(1)).create(any(SkillDto.class));
    }

    @Test
    void testCreateSkill_ValidationError() throws Exception {

        SkillDto invalidSkillDto = new SkillDto();

        mockMvc.perform(post("/api/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSkillDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Skill title cannot be empty"));

        verify(skillService, never()).create(any(SkillDto.class));
    }

    @Test
    void testCreateSkill_ServiceThrowsException() throws Exception {

        when(skillService.create(any(SkillDto.class)))
                .thenThrow(new IllegalArgumentException("Skill already exists."));

        mockMvc.perform(post("/api/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSkillDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Skill already exists."));

        verify(skillService, times(1)).create(any(SkillDto.class));
    }
}
