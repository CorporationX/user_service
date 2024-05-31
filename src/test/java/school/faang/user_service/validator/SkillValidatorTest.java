package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @InjectMocks
    private SkillValidator skillValidator;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserValidator userValidator;

    private Long skillId;
    private Long userId;
    private Long nullUserId;
    private Long nullSkillId;
    private SkillDto skillDto;
    private SkillDto skillDtoWithEmptyTitle;
    private SkillDto skillDtoWithNullTitle;

    @BeforeEach
    public void setUp() {
        skillDto = SkillDto.builder().id(1L).title("title").build();
        skillDtoWithNullTitle = SkillDto.builder().id(1L).title(null).build();
        skillDtoWithEmptyTitle = SkillDto.builder().id(1L).title(null).build();
        skillId = 1L;
        userId = 1L;
        nullUserId = null;
        nullSkillId = null;
    }

    @Test
    public void testCheckSkillIdAndUserIdInDBWithCorrectInput() {
        when(skillRepository.existsById(skillId)).thenReturn(true);

        assertDoesNotThrow(() -> userValidator.validateUserExists(userId));
        assertDoesNotThrow(() -> skillValidator.checkSkillIdAndUserIdInDB(skillId, userId));
    }

    @Test
    public void testCheckSkillIdAndUserIdInDBWithIncorrectSkillId() {
        when(skillRepository.existsById(nullSkillId)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> skillValidator.checkSkillIdAndUserIdInDB(nullSkillId, userId));
    }

    @Test
    public void testCheckSkillIdAndUserIdInDBWithIncorrectUserId() {
        when(skillRepository.existsById(skillId)).thenReturn(true);

        doThrow(DataValidationException.class).when(userValidator).validateUserExists(nullUserId);
        assertThrows(DataValidationException.class, () -> skillValidator.checkSkillIdAndUserIdInDB(skillId, nullUserId));
    }

    @Test
    public void testValidateTitleRepetitionWithUniqueInput() {
        assertDoesNotThrow(() -> skillValidator.validateTitleRepetition(skillDto.getTitle()));
    }

    @Test
    public void testValidateTitleRepetitionWithRepeatedInput() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillValidator.validateTitleRepetition(skillDto.getTitle()));
    }

    @Test
    public void testValidateSkillWithCorrectTitle() {
        assertDoesNotThrow(() -> skillValidator.validateSkill(skillDto.getTitle()));
    }

    @Test
    public void testValidateSkillWithEmptyTitle() {
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDtoWithEmptyTitle.getTitle()));
    }

    @Test
    public void testValidateSkillWithNullTitle() {
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDtoWithNullTitle.getTitle()));
    }
}
