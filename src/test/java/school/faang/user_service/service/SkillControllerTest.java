package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    public void testCreate() {
        Skill skill = new Skill();

        skillController.create(skill);

        verify(skillService, times(1)).create(skill);
    }
    @Test
    public void testGetUserSkills() {
        long userId = 1L;

        skillController.getUserSkills(userId);
        verify(skillService, times(1)).getUserSkills(userId);
    }
    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;

        skillController.getOfferedSkills(userId);
        verify(skillService, times(1)).getOfferedSkills(userId);
    }
    @Test
    public void testAcquireSkillFromOffers() {
        long userId = 1L;
        long skillId = 2L;

        skillController.acquireSkillFromOffers(skillId, userId);
        verify(skillService, times(1)).acquireSkillFromOffers(skillId, userId);
    }

}
