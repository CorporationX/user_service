package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.service.SkillService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SkillController.class)
class SkillControllerTest {

    private static final long SKILL_NEW_ID = 0L;
    private static final long SKILL_CREATED_ID = 1L;
    private static final String SKILL_TITLE = "Java";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    SkillService skillService;

    SkillDto skillDto;
    SkillDto savedSkillDto;
    Skill savedSkill;

    @BeforeEach
    public void init() {
        skillDto = new SkillDto(SKILL_NEW_ID, SKILL_TITLE);
        savedSkillDto = new SkillDto(SKILL_CREATED_ID, SKILL_TITLE);
        savedSkill = Skill.builder()
                .id(SKILL_CREATED_ID)
                .title(SKILL_TITLE)
                .build();
    }

    @Test
    public void createSkillAndReturnDtoAndStatus201() throws Exception {

        Mockito.when(skillService.create(Mockito.any())).thenReturn(savedSkillDto);

        mockMvc.perform(
                post("/skills")
                        .content(objectMapper.writeValueAsString(skillDto))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedSkillDto)));
    }

    @Test
    public void createSkillWithEmptyTitleAndGetException() throws Exception {
        SkillDto emptyDto = new SkillDto(SKILL_NEW_ID, Strings.EMPTY);

        mockMvc.perform(
                post("/skills")
                        .content(objectMapper.writeValueAsString(emptyDto))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(content().string("Skill title can't be empty"));
    }
}