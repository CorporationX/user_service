package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;
    @Mock
    private SkillService skillService;

    @Test
    void testCreateDataValidationExceptionNull() {
        SkillDto skillDto = getSkillDto(null);
        assertThrows(DataValidationException.class,
                () -> skillController.create(skillDto));
    }

    @Test
    void testCreateDataValidationExceptionBlank() {
        SkillDto skillDto = getSkillDto(" ");
        assertThrows(DataValidationException.class,
                () -> skillController.create(skillDto));
    }

    @Test
    void testCreateSuccessful() {
        SkillDto skillDto = getSkillDto("test");
        skillController.create(skillDto);
        verify(skillService).create(skillDto);
    }

    private static SkillDto getSkillDto(String test) {
        return SkillDto.builder()
                .title(test)
                .build();
    }
}