package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.AlreadyPurchasedException;
import school.faang.user_service.repository.premium.PremiumRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumValidatorTest {
    @InjectMocks
    private PremiumValidator premiumValidator;

    @Mock
    private PremiumRepository premiumRepository;

    private long userId;

    @BeforeEach
    void setUp() {
        userId = 0;
    }

    @Test
    void testValidateUserAlreadyHasPremium() {
        when(premiumRepository.existsByUserId(anyLong())).thenReturn(false);

        assertDoesNotThrow(() -> premiumValidator.validateUserAlreadyHasPremium(userId));
    }

    @Test
    void testValidateUserAlreadyHasPremiumThrowsAlreadyPremiumException() {
        when(premiumRepository.existsByUserId(anyLong())).thenReturn(true);

        assertThrows(AlreadyPurchasedException.class, () -> premiumValidator.validateUserAlreadyHasPremium(userId));
    }
}
