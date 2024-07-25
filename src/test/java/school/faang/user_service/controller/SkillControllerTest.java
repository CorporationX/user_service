package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    private MockMvc mockMvc;
    @Mock
    SkillService skillService;
    @InjectMocks
    SkillController skillController;

    private ObjectWriter objectMapper;
    private long userId;
    private long skillId;
    private String firstSkillTitle, secondSkillTitle;
    private SkillDto firstSkill, secondSkill;
    private SkillCandidateDto firstSkillCandidate, secondSkillCandidate;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(skillController).build();

        objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();

        userId = 1L;
        skillId = 1L;
        firstSkillTitle = "firstSkill";
        secondSkillTitle = "secondSkill";
        firstSkill = new SkillDto();
        secondSkill = new SkillDto();
        firstSkill.setTitle(firstSkillTitle);
        firstSkill.setId(skillId);
        firstSkill.setUserIds(List.of(userId));
        secondSkill.setTitle(secondSkillTitle);

        firstSkillCandidate = new SkillCandidateDto(firstSkill, 2);
        secondSkillCandidate = new SkillCandidateDto(secondSkill, 3);
    }

    @Test
    void testCreate() throws Exception {
        String body = objectMapper.writeValueAsString(firstSkill);
        when(skillService.createSkill(firstSkill)).thenReturn(firstSkill);

        mockMvc.perform(post("/skill")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(firstSkillTitle)));
    }

    @Test
    void testGetUserSkills() throws Exception {
        List<SkillDto> skills = List.of(firstSkill, secondSkill);
        when(skillController.getUserSkills(userId)).thenReturn(skills);

        mockMvc.perform(get("/skill")
                        .param("userId", Long.toString(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(skills.size())))
                .andExpect(jsonPath("$[0].title", is(firstSkillTitle)))
                .andExpect(jsonPath("$[1].title", is(secondSkillTitle)));
    }

    @Test
    void testGetOfferedSkills() throws Exception {
        List<SkillCandidateDto> skillCandidates = List.of(firstSkillCandidate, secondSkillCandidate);
        when(skillController.getOfferedSkills(userId)).thenReturn(skillCandidates);

        mockMvc.perform(get("/skill/offers")
                        .param("userId", Long.toString(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(skillCandidates.size())))
                .andExpect(jsonPath("$[0].skill.title", is(firstSkillTitle)))
                .andExpect(jsonPath("$[0].offersAmount",
                        is((int) firstSkillCandidate.getOffersAmount())))
                .andExpect(jsonPath("$[1].skill.title", is(secondSkillTitle)))
                .andExpect(jsonPath("$[1].offersAmount",
                        is((int) secondSkillCandidate.getOffersAmount())));
    }

    @Test
    void testAcquireSkillFromOffers() throws Exception {
        when(skillController.acquireSkillFromOffers(skillId, userId)).thenReturn(firstSkill);

        mockMvc.perform(put("/skill/acquire")
                        .param("skillId", Long.toString(skillId))
                        .param("userId", Long.toString(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) skillId)))
                .andExpect(jsonPath("$.userIds[0]", is((int) userId)));
    }
}