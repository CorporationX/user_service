package school.faang.user_service.validator.skill;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @InjectMocks
    private SkillValidator skillValidator;

    @Test
    public void testThrowExceptionWhenSkillByMinSkillOffer() {
        Assertions.assertThrows(DataValidationException.class, () -> skillValidator.validateSkillByMinSkillOffer(2, 3L, 4L));
    }

    @Test
    public void test2() {
        Assertions.assertEquals(1, 0);
    }
}
