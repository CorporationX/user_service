package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    public void create_whenNullOrBlankTitleSkill_thenThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> skillController.create(new SkillDto(null)));
        assertThrows(RuntimeException.class, () -> skillController.create(new SkillDto("   ")));
    }

    @Test
    public void getUserSkills_whenNullUserId_thenThrowRuntimeException() {
        assertThrows(RuntimeException.class, ()-> skillController.getUserSkills(0));
    }
    @Test
    public void getOfferedSkills_whenNullUserId_thenThrowRuntimeException() {
        assertThrows(RuntimeException.class, ()-> skillController.getOfferedSkills(0));
    }
}