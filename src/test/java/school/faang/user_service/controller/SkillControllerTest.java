package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;
    @Mock
    private SkillService skillService;
    SkillDto skillDto = new SkillDto(1L, "flexibility");

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

    @Test
    void testCallMethodGetOfferedSkillsFromSkillService(){
        skillController.getOfferedSkills(1L);
        verify(skillService, times(1))
                .getOfferedSkills(1L);
    }

    @Test
    void testCallMethodAcquireSkillFromOffersFromSkillService(){
        skillController.acquireSkillFromOffers(1L,1L);
        verify(skillService, times(1))
                .acquireSkillFromOffers(1L,1L);
    }
}