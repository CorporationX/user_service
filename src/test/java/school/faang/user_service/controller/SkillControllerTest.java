package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;
    @Mock
    private SkillValidation skillValidation;
    private final long userId = 1;
    private final long skillId = 1;

    @InjectMocks
    private SkillController skillController;

    @Test
    public void create_whenTitleSkillIsCorrect_thenRunService() {
        // Arrange
        SkillDto skillDto = new SkillDto(null, "Навык");

        // Act
        skillController.create(skillDto);

        // Assert
        assertAll(
                () -> verify(skillService, times(1)).create(skillDto),
                () -> verify(skillValidation, times(1)).validateSkillTitle(skillDto)
        );
    }

    @Test
    public void getUserSkills_whenUserIdIsCorrect_thenRunService() {
        // Act
        skillController.getUserSkills(userId);

        // Assert
        verify(skillService, times(1)).getUserSkills(userId);
    }

    @Test
    public void getOfferedSkills_whenUserIdIsCorrect_thenRunService() {
        // Act
        skillController.getOfferedSkills(userId);

        // Assert
        verify(skillService, times(1)).getOfferedSkills(userId);
    }

    @Test
    public void acquireSkillFromOffers_whenUserIdAndSkillIdIsCorrect_thenRunService() {
        // Act
        skillController.acquireSkillFromOffers(skillId, userId);

        // Assert
        verify(skillService, times(1)).acquireSkillFromOffers(skillId, userId);
    }
}