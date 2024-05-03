package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    public void testCreateWithNullTitle() {
        SkillDto skillDto = createSkillDto(null);
        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    public void testCreateWithEmptyTitle() {
        SkillDto skillDto = createSkillDto("");
        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    public void testWithCorrectValidationTitle() {
        SkillDto skillDto = createSkillDto("title");
        assertDoesNotThrow(() -> skillController.create(skillDto));
    }

    @Test
    public void testCreateSkill() {
        SkillDto skillDto = createSkillDto("title");
        when(skillService.create(skillDto)).thenReturn(skillDto);

        SkillDto result = skillController.create(skillDto);

        assertEquals(skillDto, result);
        verify(skillService).create(skillDto);
    }

    @Test
    public void testGetUsersSkill() {
        long userId = 1L;
        List<SkillDto> expectedSkills = Arrays.asList(new SkillDto(1L, "firstTestInfo"), new SkillDto(2L, "secondTestInfo"));
        when(skillService.getUserSkills(userId)).thenReturn(expectedSkills);

        List<SkillDto> actualSkills = skillController.getUserSkills(userId);

        assertEquals(expectedSkills, actualSkills);
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        List<SkillCandidateDto> expectedSkillCandidateDto = Arrays.asList
                (new SkillCandidateDto(new SkillDto(1L, "firstTestInfo"), 1L),
                        new SkillCandidateDto(new SkillDto(2L, "secondTestInfo"), 2L));
        when(skillService.getOfferedSkills(userId)).thenReturn(expectedSkillCandidateDto);

        List<SkillCandidateDto> candidateDtoList = skillController.getOfferedSkills(userId);

        assertEquals(expectedSkillCandidateDto, candidateDtoList);
    }

    @Test
    public void testAcquireSkillFromOffers() {
        long userId = 1;
        long skillId = 1;
        Optional<SkillDto> expectedSkillDto = Optional.of(createSkillDto("title"));
        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(expectedSkillDto);

        Optional<SkillDto> skillDtoList = skillController.acquireSkillFromOffers(skillId, userId);

        assertEquals(expectedSkillDto, skillDtoList);
    }

    private SkillDto createSkillDto(String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle(title);

        return skillDto;
    }
}
