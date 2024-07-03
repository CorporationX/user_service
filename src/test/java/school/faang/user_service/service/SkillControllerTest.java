package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @InjectMocks
    SkillController skillController;

    @Mock
    SkillService skillService;


    @Test
    void testCreateWithNullTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(null);

        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    void testCreateWithBlankTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(" ");

        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    void testCreateWhenAccess() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Title");

        skillController.create(skillDto);

        verify(skillService, times(1)).create(skillDto);
    }

    @Test
    void testGetUserSkills() {
        long userId = 1;
        List<SkillDto> skillDtos = List.of(new SkillDto(), new SkillDto());

        when(skillService.getUserSkills(userId)).thenReturn(skillDtos);
        List<SkillDto> returnedSkillDtos = skillController.getUserSkills(userId);

        verify(skillService, times(1)).getUserSkills(userId);
        assertEquals(skillDtos, returnedSkillDtos);
    }

    @Test
    void testGetOfferedSkills() {
        long userId = 1;
        List<SkillCandidateDto> skillCandidateDtos = List.of(new SkillCandidateDto(), new SkillCandidateDto());

        when(skillService.getOfferedSkills(userId)).thenReturn(skillCandidateDtos);
        List<SkillCandidateDto> returnedSkillCandidateDtos = skillController.getOfferedSkills(userId);

        verify(skillService, times(1)).getOfferedSkills(userId);
        assertEquals(skillCandidateDtos, returnedSkillCandidateDtos);
    }
}
