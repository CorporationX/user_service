package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
        skillDto.setTitle("Java");
    }

    @Test
    void testCreateSkill() {
        when(skillService.create(skillDto)).thenReturn(skillDto);

        SkillDto result = skillController.create(skillDto);

        assertNotNull(result);
        assertEquals("Java", result.getTitle());
        verify(skillService, times(1)).create(skillDto);
    }

    @Test
    void testCreateSkillWithEmptyTitle() {
        skillDto.setTitle("");

        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
        verify(skillService, never()).create(skillDto);
    }

    @Test
    void testGetUserSkills() {
        long userId = 1L;
        skillController.getUserSkills(userId);

        verify(skillService, times(1)).getUserSkills(userId);
    }

    @Test
    void testGetOfferedSkills() {
        long userId = 1L;
        List<SkillCandidateDto> expectedSkills = Collections.singletonList(new SkillCandidateDto());
        when(skillService.getOfferedSkills(userId)).thenReturn(expectedSkills);

        List<SkillCandidateDto> result = skillController.getOfferedSkills(userId);

        assertNotNull(result);
        assertEquals(expectedSkills, result);
        verify(skillService, times(1)).getOfferedSkills(userId);
    }

    @Test
    void testAcquireSkillFromOffers() {
        long skillId = 1L;
        long userId = 1L;
        SkillDto expectedSkill = new SkillDto();
        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(expectedSkill);

        SkillDto result = skillController.acquireSkillFromOffers(skillId, userId);

        assertNotNull(result);
        assertEquals(expectedSkill, result);
        verify(skillService, times(1)).acquireSkillFromOffers(skillId, userId);
    }
}