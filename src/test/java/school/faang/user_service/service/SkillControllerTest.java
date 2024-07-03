package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
