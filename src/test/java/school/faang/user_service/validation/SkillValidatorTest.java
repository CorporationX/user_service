package school.faang.user_service.validation;

import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {
    private final long ANY_ID = 123L;
    private final String BLANK_STRING = "  ";
    private final String SKILL_TITLE = "squat";
    @InjectMocks
    private SkillValidator skillValidator;
    @Mock
    private SkillRepository skillRepository;
    private SkillDto skillDto;


    @Test
    @DisplayName("Data validation exception when skill title is null")
    void whenNullValueThenThrowValidationException() {
        skillDto = SkillDto.builder()
                .id(ANY_ID)
                .title(null)
                .build();

        assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skillDto), "Skill title can't be blank or null");
    }

    @Test
    @DisplayName("Data validation exception when skill title is blank")
    void whenEmptyValueThenThrowValidationException() {
        skillDto = SkillDto.builder()
                .id(ANY_ID)
                .title(BLANK_STRING)
                .build();

        assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skillDto), "Skill title can't be blank or null");
    }

    @Test
    @DisplayName("Data validation exception when the skill we want to create already exist")
    void whenSkillExistThenThrowException() {
        skillDto = SkillDto.builder()
                .id(ANY_ID)
                .title(SKILL_TITLE)
                .build();

        when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skillDto), "Skill \"" + skillDto.getTitle() + "\" already exist");
    }

    @Test
    @DisplayName("Data validation exception when SKillDto is null")
    void whenNullDtoThenThrowValidationException() {
        assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(null), "SkillDto can't be null");
    }

    @Test
    @DisplayName("Error when user already have the skill we want to add")
    void whenUserAlreadyHaveSkillThenThrowException() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.of(new Skill()));

        assertThrows(DataValidationException.class,
                () -> skillValidator.checkUserSkill(ANY_ID, ANY_ID), "User with id:" + ANY_ID + " already have skill with id" + ANY_ID);
    }

}