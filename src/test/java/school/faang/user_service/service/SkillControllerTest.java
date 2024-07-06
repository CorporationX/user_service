package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
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
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    private SkillDto skillDto;
    private long userId = 1;
    private long skillId = 1;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
    }

    @Test
    void testCreateWithNullTitle() {
        skillDto.setTitle(null);

        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    void testCreateWithBlankTitle() {
        skillDto.setTitle(" ");

        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    void testCreateWhenAccess() {
        skillDto.setTitle("Title");

        skillController.create(skillDto);

        verify(skillService, times(1)).create(skillDto);
    }

    @Test
    void testGetUserSkills() {
        List<SkillDto> skillDtos = List.of(new SkillDto(), new SkillDto());

        when(skillService.getUserSkills(userId)).thenReturn(skillDtos);
        List<SkillDto> returnedSkillDtos = skillController.getUserSkills(userId);

        verify(skillService, times(1)).getUserSkills(userId);
        assertEquals(skillDtos, returnedSkillDtos);
    }

    @Test
    void testGetOfferedSkills() {
        List<SkillCandidateDto> skillCandidateDtos = List.of(new SkillCandidateDto(), new SkillCandidateDto());

        when(skillService.getOfferedSkills(userId)).thenReturn(skillCandidateDtos);
        List<SkillCandidateDto> returnedSkillCandidateDtos = skillController.getOfferedSkills(userId);

        verify(skillService, times(1)).getOfferedSkills(userId);
        assertEquals(skillCandidateDtos, returnedSkillCandidateDtos);
    }

    @Test
    void testGetAcquireSkillFromOffer() {
        skillDto.setId(1L);

        when(skillService.acquireSkillFromOffer(skillId, userId)).thenReturn(skillDto);

        SkillDto returnedSkillDto = skillController.acquireSkillFromOffer(skillId, userId);

        verify(skillService, times(1)).acquireSkillFromOffer(skillId, userId);
        assertEquals(skillDto, returnedSkillDto);
    }
}
