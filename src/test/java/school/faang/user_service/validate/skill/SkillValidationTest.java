package school.faang.user_service.validate.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.skill.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillValidationTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    long userId = 1L;
    long skillId = 1L;

    @InjectMocks
    private SkillValidation skillValidation;

    @Test
    void validateNullUserId() {
        assertThrows(DataValidationException.class, () -> skillValidation.validateNullUserId(0));
    }

    @Test
    void validateNullSkillId() {
        assertThrows(DataValidationException.class, () -> skillValidation.validateNullSkillId(0));
    }

    @Test
    void validateUserId() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataValidationException.class, () -> skillValidation.validateUserId(userId));
    }

    @Test
    void validateSkillId() {
        // Arrange
        when(skillRepository.existsById(skillId)).thenReturn(false);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> skillValidation.validateSkillId(skillId));
    }

    @Test
    void validateSkillTitle_whenNullOrBlankTitleSkill_thenThrowDataValidationException() {
        // Arrange
        SkillDto skillDtoNull = new SkillDto(null, null);
        SkillDto skillDtoSpace = new SkillDto(null, "    ");

        // Act & Assert
        assertThrows(DataValidationException.class, () -> skillValidation.validateSkillTitle(skillDtoNull));
        assertThrows(DataValidationException.class, () -> skillValidation.validateSkillTitle(skillDtoSpace));
    }
}