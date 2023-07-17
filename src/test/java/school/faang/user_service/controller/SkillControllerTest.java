package school.faang.user_service.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;
    @Mock
    private SkillService skillService;
    SkillDto skillDto = new SkillDto(1L, "flexibility");

    @Test
    void create() {
    }
    @Test
    void testBlankTitleIsInvalid() {
        assertThrows(DataValidationException.class,
                () -> skillController.validateSkill(new SkillDto(1L, "   ")));
    }

    @Test
    void testNullTitleIsInvalid() {
        assertThrows(DataValidationException.class,
                () -> skillController.validateSkill(new SkillDto(1L, null)));
    }

    @Test
    void testTitleIsValid() {
        assertDoesNotThrow(
                () -> skillController.validateSkill(skillDto));
    }

    @Test
    void createNewSkill() {
        when(skillService.create(skillDto)).thenReturn(skillDto);
        SkillDto skillDto1 = skillController.create(skillDto);
        verify(skillService, times(1))
                .create(skillDto);
        assertEquals(skillDto, skillDto1);
    }

    @Test
    void testCallMethodGetUserSkillsFromSkillService(){
        skillController.getUserSkills(1L);
        verify(skillService, times(1))
                .getUserSkills(1L);
    }
}