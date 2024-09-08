package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.dto.Skill.SkillDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    public void testCreate() {
        SkillDto skillDto = new SkillDto("Java", 1L);
        SkillDto createdSkillDto = new SkillDto("Java", 1L);
        when(skillService.create(skillDto)).thenReturn(createdSkillDto);

        SkillDto actualSkillDto = skillController.create(skillDto);
        assertEquals(createdSkillDto, actualSkillDto);
        verify(skillService, times(1)).create(skillDto);
    }
    @Test
    public void testGetUserSkills() {
        long userId = 1L;
        List<SkillDto> expectedSkills = List.of(new SkillDto("Java", userId), new SkillDto("Python", userId));
        when(skillService.getUserSkills(userId)).thenReturn(expectedSkills);

        List<SkillDto> actualSkills = skillController.getUserSkills(userId);
        assertEquals(expectedSkills, actualSkills);
        verify(skillService, times(1)).getUserSkills(userId);
    }
    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        SkillDto dto1 = new SkillDto("Java", 1L);
        SkillDto dto2 = new SkillDto("Python", 2L);
        List<SkillCandidateDto> expectedSkills = List.of(new SkillCandidateDto(dto1, userId), new SkillCandidateDto(dto2, userId));
        when(skillService.getOfferedSkills(userId)).thenReturn(expectedSkills);

        List<SkillCandidateDto> actualSkills = skillController.getOfferedSkills(userId);
        assertEquals(expectedSkills, actualSkills);
        verify(skillService, times(1)).getOfferedSkills(userId);
    }
    @Test
    public void testAcquireSkillFromOffers() {
        long userId = 1L;
        long skillId = 2L;
        SkillDto expectedSkill = new SkillDto("Java", userId);
        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(expectedSkill);

        SkillDto actualSkill = skillController.acquireSkillFromOffers(skillId, userId);
        assertEquals(expectedSkill, actualSkill);
        verify(skillService, times(1)).acquireSkillFromOffers(skillId, userId);
    }

}
