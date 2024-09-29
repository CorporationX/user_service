package school.faang.user_service.validator.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.premium.PremiumRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumValidatorTest {

    private static final Long USER_ID = 1L;

    @InjectMocks
    private PremiumValidator premiumValidator;

    @Mock
    private PremiumRepository premiumRepository;

    @Test
    @DisplayName("Should throw exception if premium already exists")
    void whenPremiumAlreadyExistsThenThrowException() {
        when(premiumRepository.existsByUserId(USER_ID)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            premiumValidator.validatePremiumAlreadyExistsByUserId(USER_ID);
        });

        assertEquals("Premium already exists for userId: " + USER_ID, exception.getMessage());
        verify(premiumRepository, times(1)).existsByUserId(USER_ID);
    }

    @Test
    @DisplayName("Should not throw exception if premium does not exist")
    void whenPremiumDoesNotExistThenDoNothing() {
        when(premiumRepository.existsByUserId(USER_ID)).thenReturn(false);

        premiumValidator.validatePremiumAlreadyExistsByUserId(USER_ID);

        verify(premiumRepository, times(1)).existsByUserId(USER_ID);
    }
}
