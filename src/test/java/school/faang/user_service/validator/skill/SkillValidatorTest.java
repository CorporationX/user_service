package school.faang.user_service.validator.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {
    @InjectMocks
    private SkillValidator skillValidator;

    @Mock
    private SkillRepository skillRepository;

    private final long skillId = 1L;
    private final long userId = 1L;
    private final int enoughOffers = 3;
    private final int notEnoughOffers = 2;
    private SkillDto skillDto;

    @Test
    void validateSkillByTitle_whenSkillExists_shouldReturnDataValidationException() {
        // given
        skillDto = prepareData();
        when(skillRepository.existsByTitle("Title")).thenReturn(true);
        // when/then
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkillByTitle(skillDto));
    }

    @Test
    void validateSkillByTitle_whenSkillNotExists_shouldNotThrowDataValidationException() {
        // given
        skillDto = prepareData();
        when(skillRepository.existsByTitle("Title")).thenReturn(false);
        // when/then
        assertDoesNotThrow(() -> skillValidator.validateSkillByTitle(skillDto));
    }

    @Test
    void validateOfferedSkill_whenUserHasNoSkill_shouldNotThrowDataValidationException() {
        // given
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        // when/then
        assertDoesNotThrow(() -> skillValidator.validateOfferedSkill(skillId, userId));
    }

    @Test
    void validateOfferedSkill_whenUserAlreadyHasSkill_shouldThrowDataValidationException() {
        // given
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.ofNullable(Skill.builder().build()));
        // when/then
        assertThrows(DataValidationException.class, () -> skillValidator.validateOfferedSkill(skillId, userId));
    }

    @Test
    void validateSkillByMinSkillOffers_whenNotEnoughOffers_shouldThrowDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillByMinSkillOffers(notEnoughOffers, skillId, userId));
    }

    @Test
    void validateSkillByMinSkillOffers_whenEnoughOffers_shouldNotThrowDataValidationException() {
        assertDoesNotThrow(() -> skillValidator.validateSkillByMinSkillOffers(enoughOffers, skillId, userId));
    }

    private SkillDto prepareData() {
        return SkillDto.builder()
                .id(skillId)
                .title("Title")
                .build();
    }
}