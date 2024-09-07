package school.faang.user_service.validator.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {
    @InjectMocks
    private SkillServiceValidator skillValidator;

    @Mock
    private SkillRepository skillRepository;

    @Test
    void validateSkill_whenTitleIsBlank_shouldReturnDataValidationException() {
        // given
        SkillDto skillDto = new SkillDto(1L, " ");
        // when/then
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDto));
    }

    @Test
    void validateSkill_whenSkillExists_shouldReturnDataValidationException() {
        // given
        SkillDto skillDto = new SkillDto(1L, "Title");
        when(skillRepository.existsByTitle("Title")).thenReturn(true);
        // when/then
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDto));
    }

    @Test
    void validateSkill_whenSkillNotExists_shouldNotThrowDataValidationException() {
        // given
        SkillDto skillDto = new SkillDto(1L, "Title");
        when(skillRepository.existsByTitle("Title")).thenReturn(false);
        // when/then
        assertDoesNotThrow(() -> skillValidator.validateSkill(skillDto));
    }

    @Test
    void validateOfferedSkill_whenUserAlreadyHasSkill_shouldThrowDataValidationException() {
        // given
        long skillId = 1L;
        long userId = 1L;
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.ofNullable(Skill.builder().build()));
        // when/then
        assertThrows(DataValidationException.class, () -> skillValidator.validateOfferedSkill(skillId, userId));
    }

    @Test
    void validateOfferedSkill_whenUserHasNoSkill_shouldNotThrowDataValidationException() {
        // given
        long skillId = 1L;
        long userId = 1L;
        // when/then
        assertDoesNotThrow(() -> skillValidator.validateOfferedSkill(skillId, userId));
    }

    @Test
    void validateSkillByMinSkillOffers_whenEnoughOffers_shouldNotThrowDataValidationException() {
        long skillId = 1L;
        long userId = 1L;
        int enoughOffers = 3;
        assertDoesNotThrow(() -> skillValidator.validateSkillByMinSkillOffers(enoughOffers, skillId, userId));

    }

    @Test
    void validateSkillByMinSkillOffers_whenNotEnoughOffers_shouldThrowDataValidationException() {
        long skillId = 1L;
        long userId = 1L;
        int notEnoughOffers = 2;
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkillByMinSkillOffers(notEnoughOffers, skillId, userId));
    }
}