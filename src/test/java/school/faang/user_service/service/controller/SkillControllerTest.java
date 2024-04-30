package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

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
        skillDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    public void testCreateWithEmptyTitle() {
        SkillDto skillDto = createSkillDto("");
        skillDto.setTitle("");
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

    private SkillDto createSkillDto(String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle(title);

        return skillDto;
    }
}
