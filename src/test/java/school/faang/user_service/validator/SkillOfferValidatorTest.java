package school.faang.user_service.validator;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillOfferValidatorTest {

    @InjectMocks
    private SkillOfferValidator skillOfferValidator;
    @Mock
    private SkillRepository skillRepository;

    private List<SkillOfferDto> skills;

    @BeforeEach
    void setUp() {
        skills = List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L));
    }

    @Test
    public void testValidateSkillsListNotEmptyOrNull_PassValidation() {
        assertDoesNotThrow(() -> skillOfferValidator.validateSkillsListNotEmptyOrNull(skills));
    }

    @Test
    public void testValidateSkillsListNotEmptyOrNull_SkillListIsNull() {
        String message = "You should choose some skills";

        assertThrows(DataValidationException.class,
                () -> skillOfferValidator.validateSkillsListNotEmptyOrNull(null), message);
    }

    @Test
    public void testValidateSkillsListNotEmptyOrNull_SkillListIsEmpty() {
        List<SkillOfferDto> emptySkills = new ArrayList<>();
        String message = "You should choose some skills";

        assertThrows(DataValidationException.class,
                () -> skillOfferValidator.validateSkillsListNotEmptyOrNull(emptySkills), message);
    }

    @Test
    public void testValidateSkillsAreInRepository_ValidSkills() {
        when(skillRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> skillOfferValidator.validateSkillsAreInRepository(skills));
    }

    @Test
    public void testValidateSkillsAreInRepository_InvalidSkills() {
        String message = "Invalid skills";
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> skillOfferValidator.validateSkillsAreInRepository(skills), message);
    }
}