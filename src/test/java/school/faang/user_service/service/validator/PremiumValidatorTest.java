package school.faang.user_service.service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PremiumValidator;

@ExtendWith(MockitoExtension.class)
public class PremiumValidatorTest {

    @Mock
    private PremiumRepository premiumRepo;


    @InjectMocks
    private PremiumValidator premiumValidator;

    @Test
    public void validatePremiumSuccess() {
        Mockito.when(premiumRepo.existsById(1L)).thenReturn(true);
        Assert.assertThrows(DataValidationException.class,
                () -> premiumValidator.validatePremium(1L));
    }
}
