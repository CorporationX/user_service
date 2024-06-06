package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserFilterDtoValidatorTest {
    @InjectMocks
    private UserFilterDtoValidator userFilterDtoValidator;

    @Test
    public void testCheckUserFilterDtoIsNull() {
        assertThrows(DataValidationException.class, () -> userFilterDtoValidator.checkUserFilterDtoIsNull(null));
    }
}
