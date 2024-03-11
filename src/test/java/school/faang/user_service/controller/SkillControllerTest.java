package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidator;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    SkillController skillController;

    @Mock
    SkillService skillService;
    @Mock
    SkillValidator skillValidator;

    Skill firstSkill;
    Skill secondSkill;
    List<Skill> skills;
    SkillDto firstSkillDto;
    SkillDto secondSkillDto;
    List<SkillDto> skillDtos;
    User user;

    @BeforeEach
    public void init () {
        mockMvc = MockMvcBuilders.standaloneSetup(skillController).build();
        firstSkill = Skill.builder().title("java").id(1L).build();
        secondSkill = Skill.builder().title("spring").id(2L).build();
        firstSkillDto = SkillDto.builder().title("java").id(1L).build();
        secondSkillDto = SkillDto.builder().title("spring").id(2L).build();
        skills = Arrays.asList(firstSkill, secondSkill);
        user = User.builder().username("John").id(1L).skills(skills).build();
    }

    @Test
    public void shouldGetUserSkills () throws Exception {
        skillDtos = Arrays.asList(firstSkillDto, secondSkillDto);
        when(skillService.getUserSkills(user.getId())).thenReturn(skillDtos);

        mockMvc.perform(get("/skill/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("java")))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].title", is("spring")))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    public void shouldCreateSkill () throws Exception {
        when(skillService.create(ArgumentMatchers.any())).thenReturn(firstSkillDto);

        mockMvc.perform(post("/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(firstSkillDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("java")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void shouldGetOfferedSkills () throws Exception {
        SkillCandidateDto firstSkillCandidateDto = SkillCandidateDto.builder()
                .offersAmount(3L)
                .skill(firstSkillDto)
                .build();
        SkillCandidateDto secondSkillCandidateDto = SkillCandidateDto.builder()
                .offersAmount(1L)
                .skill(secondSkillDto)
                .build();
        List<SkillCandidateDto> skillCandidateDtos = Arrays.asList(firstSkillCandidateDto, secondSkillCandidateDto);

        when(skillService.getOfferedSkills(user.getId())).thenReturn(skillCandidateDtos);

        mockMvc.perform(get("/skill/1/offered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].offersAmount", is(3)))
                .andExpect(jsonPath("$[0].skill.title", is("java")))
                .andExpect(jsonPath("$[0].skill.id", is(1)))
                .andExpect(jsonPath("$[1].offersAmount", is(1)))
                .andExpect(jsonPath("$[1].skill.title", is("spring")))
                .andExpect(jsonPath("$[1].skill.id", is(2)));
    }

    @Test
    public void shouldGetSkillFromOffers () throws Exception {
        when(skillService.acquireSkillFromOffers(firstSkill.getId(), user.getId()))
                .thenReturn(firstSkillDto);

        mockMvc.perform(post("/skill/1/offered/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("java")))
                .andExpect(jsonPath("$.id", is(1)));
    }
}
