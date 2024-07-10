package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    public void testCreateValidSkillReturnsSkillDto() {
        SkillDto skillDto = new SkillDto(2L, "Skill");
        skillDto.setTitle("Skill");
        Mockito.when(skillService.create(any(SkillDto.class))).thenReturn(skillDto);
        SkillDto result = skillController.create(skillDto);
        assertThat(result).isEqualTo(skillDto);
    }

    @Test
    public void testCreateEmptyTitleThrowsDataValidationException() {
        SkillDto skillDto = new SkillDto(2L, "Skill");
        skillDto.setTitle(" ");
        assertThatThrownBy(() -> skillController.create(skillDto))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("Skill title cannot be empty");
    }
}