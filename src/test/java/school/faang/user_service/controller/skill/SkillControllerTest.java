package school.faang.user_service.controller.skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @Mock
    private SkillService skillService;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private SkillController skillController;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Test
    public void testCreateNullTitle() {
        CreateSkillDto dto = new CreateSkillDto(null);

        assertThrows(DataValidationException.class, () -> skillController.create(dto));
    }

    @Test
    public void testCreateBlankTitle() {
        CreateSkillDto dto = new CreateSkillDto("   ");

        assertThrows(DataValidationException.class, () -> skillController.create(dto));
    }

    @Test
    public void testCreateCallService() {
        CreateSkillDto dto = new CreateSkillDto("title");

        skillController.create(dto);

        verify(skillService, times(1)).create(dto);
    }

    @Test
    public void testGetByUserIdNullUserId() {
        assertThrows(DataValidationException.class, () -> skillController.getByUserId(null));
    }

    @Test
    public void testGetByUserIdCallService() {
        Long userId = 1L;
        skillController.getByUserId(userId);

        verify(skillService, times(1)).getByUserId(userId);
    }

    @Test
    public void testGetOfferedSkillsCallService() {
        Long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        skillController.getOfferedSkills();

        verify(skillService, times(1)).getOfferedSkills(longArgumentCaptor.capture());
        assertEquals(userId, longArgumentCaptor.getValue());
    }

    @Test
    public void testAcquireSkillFromOffersCallService() {
        Long userId = 1L;
        Long skillId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        skillController.acquireSkillFromOffers(skillId);

        verify(skillService, times(1)).acquireSkillFromOffers(eq(skillId), longArgumentCaptor.capture());
        assertEquals(userId, longArgumentCaptor.getValue());
    }
}
