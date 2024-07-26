package school.faang.user_service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

@Nested
@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;


    @Test
    @DisplayName("testValidateSkillTitleIsBlank")
    void validateSkillTitleIsBlank() {
        SkillDto skillDto = SkillDto.builder()
                .title("  ").build();

        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillTitleIsNotNullAndNotBlank(skillDto));
    }

    @Test
    @DisplayName("testValidateSkillTitleIsNull")
    void testValidateSkillTitleIsNull() {
        SkillDto skillDto = new SkillDto();

        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillTitleIsNotNullAndNotBlank(skillDto));
    }

    @Test
    @DisplayName("testValidateSkillTitleDosNotExists")
    void testValidateSkillTitleDosNotExists() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("some title");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillTitleDosNotExists(skillDto));

    }

    @Test
    void testValidateSkillTitleExists() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("some title");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> skillValidator.validateSkillTitleDosNotExists(skillDto));
    }


    @Test
    @DisplayName("testValidateSkillExistenceById")
    void testValidateSkillExistenceById() {
        Long skillId = 1L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillExistenceById(skillId));
    }

    @Test
    @DisplayName("testValidateSkillExistenceByDoesNotThrowException")
    void testValidateSkillExistenceByDoesNotThrowException() {
        Long skillId = 1L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(new Skill()));

        Assertions.assertDoesNotThrow(() -> skillValidator.validateSkillExistenceById(skillId));
    }

}
