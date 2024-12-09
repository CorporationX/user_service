package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.skill.SkillRepository;
import school.faang.user_service.repository.SkillOfferRepository;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @InjectMocks
    private SkillValidator skillValidator;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Test
    void testValidateExistTitle() {
        String title = "title";

        when(skillRepository.existsByTitle(title)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillValidator.validateExistTitle(title));
    }

    @Test
    void testValidateNotExistTitle() {
        String title = "title";

        when(skillRepository.existsByTitle(title)).thenReturn(false);

        assertDoesNotThrow(() -> skillValidator.validateExistTitle(title));
    }



    @Test
    void testValidateSkillOfferCount() {
        long skillId = 1;
        long userId = 1;

        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(Arrays.asList(new SkillOffer()));

        assertThrows(DataValidationException.class, () -> skillValidator.validateSkillOfferCount(skillId, userId));

    }
}
