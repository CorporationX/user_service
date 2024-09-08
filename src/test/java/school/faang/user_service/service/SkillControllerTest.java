package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.dto.Skill.SkillDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    public void testCreate() {
        SkillDto skillDto = new SkillDto("Java",1L);

        skillController.create(skillDto);
        verify(skillService, times(1)).create(skillDto);
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
