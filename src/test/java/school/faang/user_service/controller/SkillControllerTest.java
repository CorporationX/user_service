package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @Mock
    private SkillService skillService;

    private SkillDto skillDto;
    private SkillCandidateDto skillCandidateDto;
    private long userId;
    private long skillId;

    @InjectMocks
    private SkillController skillController;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
        skillCandidateDto = new SkillCandidateDto();
        userId = 1L;
        skillId = 1L;
    }

    @Test
    @DisplayName("testCreate")
    void testCreate() {
        when(skillService.create(skillDto)).thenReturn(skillDto);

        SkillDto skillDtoResult = skillController.create(skillDto);

        verify(skillService).create(skillDto);
        assertEquals(skillDto, skillDtoResult);
    }

    @Test
    @DisplayName("testGetUserSkills")
    void testGetUserSkills() {
        when(skillService.getUserSkills(userId)).thenReturn(List.of(skillDto));

        List<SkillDto> userSkillsResult = skillController.getUserSkills(userId);

        verify(skillService).getUserSkills(userId);
        assertEquals(List.of(skillDto), userSkillsResult);
    }

    @Test
    @DisplayName("testGetOfferedSkills")
    void testGetOfferedSkills() {
        when(skillService.getOfferedSkills(userId)).thenReturn(List.of(skillCandidateDto));

        List<SkillCandidateDto> offeredSkillsResult = skillController.getOfferedSkills(userId);

        verify(skillService).getOfferedSkills(userId);
        assertEquals(List.of(skillCandidateDto), offeredSkillsResult);
    }

    @Test
    @DisplayName("testAcquireSkillFromOffers")
    void testAcquireSkillFromOffers() {
        when(skillService.acquireSkillFromOffers(skillId, userId))
                .thenReturn(skillDto);

        SkillDto skillDtoResult = skillController.acquireSkillFromOffers(skillId, userId);

        verify(skillService).acquireSkillFromOffers(skillId, userId);
        assertEquals(skillDto, skillDtoResult);
    }

}