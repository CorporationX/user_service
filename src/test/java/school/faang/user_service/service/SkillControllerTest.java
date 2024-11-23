package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    private MockMvc mockMvc;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @BeforeEach
    public void setUp() {
       mockMvc = MockMvcBuilders.standaloneSetup(skillController).build();
    }

    public static SkillDto anySkillDto(String title) {
        SkillDto skill = new SkillDto();
        skill.setTitle(title);
        return skill;
    }

    @Test
    public void testCreateSkillPositive() {

        SkillDto skillDto = anySkillDto("title");
        when(skillService.create(skillDto)).thenReturn(skillDto);

        skillController.create(skillDto);
        verify(skillService, times(1)).create(skillDto);
    }

    @Test
    public void testGetUserSkills() throws Exception {
        List<SkillDto> expectedSkills = Arrays.asList(new SkillDto(),
                new SkillDto());

        when(skillService.getUserSkills(1L)).thenReturn(expectedSkills);

        mockMvc.perform(get("/skill/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetOfferedSkills() throws Exception {
        List<SkillCandidateDto> expectedSkills = Arrays.asList(new SkillCandidateDto(),
                new SkillCandidateDto());

        when(skillService.getOfferedSkills(1L)).thenReturn(expectedSkills);

        mockMvc.perform(get("/skill?userId=1")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testAcquireSkillFromOffers() throws Exception {
        long skillId = 1L;
        long userId = 1L;
        Optional<SkillDto> expectedSkill = Optional.of(new SkillDto(skillId, "skill"));

        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(expectedSkill);

        mockMvc.perform(put("/skill")
                        .param("skillId", String.valueOf(skillId))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
}
