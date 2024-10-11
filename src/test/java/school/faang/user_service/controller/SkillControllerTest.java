package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.controller.skill.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    void testNormalTitleCreate() {
        SkillDto skillDto1 = new SkillDto();
        Mockito.when(skillService.create(skillDto1)).thenReturn(null);
        skillDto1.setTitle("title");

        assertNull(skillController.create(skillDto1));
    }

    @Test
    void testGetUserSkills() {
        Mockito.when(skillService.getUserSkills(1L)).thenReturn(null);

        assertNull(skillController.getUserSkills(1L));
    }

    @Test
    void testGetOfferedSkills() {
        Mockito.when(skillService.getOfferedSkills(1L)).thenReturn(null);

        assertNull(skillController.getOfferedSkills(1L));
    }

    @Test
    void testAcquireSkillFromOffers() {
        Mockito.when(skillService.acquireSkillFromOffers(1L, 1L)).thenReturn(null);

        assertNull(skillController.acquireSkillFromOffers(1L, 1L));
    }

}