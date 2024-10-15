package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.model.dto.SkillCandidateDto;
import school.faang.user_service.model.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SkillAssignmentException;
import school.faang.user_service.exception.handler.GlobalRestExceptionHandler;
import school.faang.user_service.service.impl.SkillServiceImpl;
import school.faang.user_service.validator.SkillValidator;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    private final Long validId = 1L;
    private SkillDto skillDto;
    private SkillDto anotherSkillDto;
    private SkillCandidateDto skillCandidateDto;
    private SkillCandidateDto anotherSkillCandidateDto;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Mock
    private SkillServiceImpl skillService;

    @InjectMocks
    private SkillController skillController;

    @BeforeEach
    public void setUp() {
        skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("New Skill");

        anotherSkillDto = new SkillDto();
        anotherSkillDto.setId(2L);
        anotherSkillDto.setTitle("Yet Another Skill");

        skillCandidateDto = new SkillCandidateDto(skillDto, 3L);
        anotherSkillCandidateDto = new SkillCandidateDto(anotherSkillDto, 5L);

        objectMapper = new ObjectMapper();

        SkillValidator skillValidator = new SkillValidator();
        ReflectionTestUtils.setField(skillController, "validator", skillValidator);

        mockMvc = MockMvcBuilders.standaloneSetup(skillController)
                .setControllerAdvice(new GlobalRestExceptionHandler())
                .build();
    }

    @AfterEach
    public void tearDown() {
        skillDto = anotherSkillDto = null;
        skillCandidateDto = anotherSkillCandidateDto = null;
    }

    @Test
    @DisplayName("Should successfully create a skill and return SkillDto when skill is valid")
    public void testCreateSkill_Success() throws Exception {
        String requestBody = objectMapper.writeValueAsString(skillDto);
        when(skillService.create(any(SkillDto.class))).thenReturn(skillDto);

        mockMvc.perform(post("/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Skill"));

        verify(skillService, times(1)).create(any(SkillDto.class));
    }

    @Test
    @DisplayName("Should throw DataValidationException when skill title is empty")
    public void testValidateSkill_InvalidTitle() {
        SkillDto invalidSkillDto = new SkillDto();
        invalidSkillDto.setTitle("");

        DataValidationException thrown = assertThrows(DataValidationException.class, () ->
                skillController.create(invalidSkillDto));

        assertEquals("Skill must have a non-empty title.", thrown.getMessage());

        verify(skillService, never()).create(any(SkillDto.class));
    }

    @Test
    @DisplayName("Should return a list of SkillDto for the user when skills are available")
    public void testGetUserSkills_Success() throws Exception {
        List<SkillDto> skillDtos = List.of(skillDto, anotherSkillDto);
        when(skillService.getUserSkills(anyLong())).thenReturn(skillDtos);

        mockMvc.perform(get("/skills/{userId}", validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(skillDtos)));

        verify(skillService, times(1)).getUserSkills(anyLong());
    }

    @Test
    @DisplayName("Should return an empty list when no skills are available for the user")
    public void testGetUserSkills_EmptyList() throws Exception {
        when(skillService.getUserSkills(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/skills/{userId}", validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(content().json("[]"));

        verify(skillService, times(1)).getUserSkills(anyLong());
    }

    @Test
    @DisplayName("Should return list of offered skills for valid userId")
    public void testGetOfferedSkills_Success() throws Exception {
        List<SkillCandidateDto> offeredSkills = List.of(skillCandidateDto, anotherSkillCandidateDto);
        when(skillService.getOfferedSkills(anyLong())).thenReturn(offeredSkills);

        mockMvc.perform(get("/skills/{userId}/offers", validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].skill.title").value("New Skill"))
                .andExpect(jsonPath("$[0].offersAmount").value(3))
                .andExpect(jsonPath("$[1].skill.title").value("Yet Another Skill"))
                .andExpect(jsonPath("$[1].offersAmount").value(5));

        verify(skillService, times(1)).getOfferedSkills(anyLong());
    }

    @Test
    @DisplayName("Should return empty list when no offered skills are available for the user")
    public void testGetOfferedSkills_EmptyList() throws Exception {
        when(skillService.getOfferedSkills(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/skills/{userId}/offers", validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(content().json("[]"));

        verify(skillService, times(1)).getOfferedSkills(anyLong());
    }

    @Test
    @DisplayName("Should return SkillDto when skill is successfully acquired")
    public void testAcquireSkillFromOffers_Success() throws Exception {
        String requestBody = objectMapper.writeValueAsString(skillDto);
        when(skillService.acquireSkillFromOffers(anyLong(), anyLong())).thenReturn(skillDto);

        mockMvc.perform(post("/skills/{skillId}/beneficiary/{userId}", skillDto.getId(), validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(requestBody));

        verify(skillService, times(1)).acquireSkillFromOffers(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should return HTTP 409 when skill is already acquired")
    public void testAcquireSkillFromOffers_Conflict() throws Exception {
        when(skillService.acquireSkillFromOffers(anyLong(), anyLong()))
                .thenThrow(new SkillAssignmentException("Skill already acquired by the user."));

        mockMvc.perform(post("/skills/{skillId}/beneficiary/{userId}", skillDto.getId(), validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Skill Assignment Conflict"))
                .andExpect(jsonPath("$.message").value("Skill already acquired by the user."));

        verify(skillService, times(1)).acquireSkillFromOffers(anyLong(), anyLong());
    }
}
