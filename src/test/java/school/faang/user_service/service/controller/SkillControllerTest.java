package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    public void testReturnCreatedSkill() {
        SkillDto skillDto = new SkillDto();

        when(skillService.create(any())).thenReturn(skillDto);

        SkillDto result = skillController.create(skillDto);

        assertNotNull(result);
        assertEquals(skillDto, result);
    }

    @Test
    public void testReturnUserSkillsList() {
        long userId = 1L;
        SkillDto skillDto1 = new SkillDto();
        skillDto1.setTitle("Java");
        SkillDto skillDto2 = new SkillDto();
        skillDto2.setTitle("Bootcamp");
        List<SkillDto> skills = List.of(skillDto1, skillDto2);

        when(skillService.getUserSkills(userId)).thenReturn(skills);

        List<SkillDto> result = skillController.getUserSkills(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getTitle());
        assertEquals("Bootcamp", result.get(1).getTitle());
    }

    @Test
    public void testReturnOfferedSkillsList() {
        long userId = 1L;

        List<SkillCandidateDto> offeredSkills = List.of(new SkillCandidateDto(), new SkillCandidateDto());

        when(skillService.getOfferedSkills(userId)).thenReturn(offeredSkills);

        List<SkillCandidateDto> result = skillController.getOfferedSkills(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testReturnAcquiredSkills() {
        long skillId = 10L;
        long userId = 1L;
        SkillDto skillDto = new SkillDto();

        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(skillDto);

        SkillDto result = skillController.acquireSkillFromOffers(skillId, userId);

        assertNotNull(result);
        assertEquals(skillDto, result);
    }
}
