package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    public void validateSkill_whenNullTitleSkill_thenThrowRuntimeException() {
        Assert.assertThrows(RuntimeException.class, () -> skillController.validateSkill(new SkillDto( null)));
    }

    @Test
    public void validateSkill_whenBlankTitleSkill_thenThrowRuntimeException() {
        Assert.assertThrows(RuntimeException.class, () -> skillController.validateSkill(new SkillDto( "   ")));
    }
}