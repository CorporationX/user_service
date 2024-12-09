package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    private final static int SKILL_ID = 1;
    private final static int USER_Id = 1;

    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private SkillService skillService;
    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
    }

    @Test
    void testGetUserSkills() {
        skillService.getUserSkills(USER_Id);

        verify(skillService, times(1)).getUserSkills(USER_Id);
    }

    @Test
    void testGetOfferedSkill() {
        skillService.getOfferedSkills(USER_Id);

        verify(skillService, times(1)).getOfferedSkills(USER_Id);
    }

    @Test
    void acquireSkillFromOffers() {
        skillService.acquireSkillFromOffers(SKILL_ID, USER_Id);

        verify(skillService, times(1)).acquireSkillFromOffers(SKILL_ID, USER_Id);
    }
}
