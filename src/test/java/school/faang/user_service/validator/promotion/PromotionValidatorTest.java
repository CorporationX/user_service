package school.faang.user_service.validator.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.validator.promotion.PromotionValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionValidatorTest {

    private static final Long USER_ID = 1L;

    @InjectMocks
    private PromotionValidator promotionValidator;

    @Mock
    private PromotionRepository promotionRepository;

    @Test
    @DisplayName("Should throw exception if promotion already exists")
    void whenPromotionAlreadyExistsThenThrowException() {
        when(promotionRepository.existsByUserId(USER_ID)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            promotionValidator.validatePromotionAlreadyExistsByUserId(USER_ID);
        });

        assertEquals("Promotion already exists for userId: " + USER_ID, exception.getMessage());
        verify(promotionRepository, times(1)).existsByUserId(USER_ID);
    }

    @Test
    @DisplayName("Should not throw exception if promotion does not exist")
    void whenPromotionDoesNotExistThenDoNothing() {
        when(promotionRepository.existsByUserId(USER_ID)).thenReturn(false);

        promotionValidator.validatePromotionAlreadyExistsByUserId(USER_ID);

        verify(promotionRepository, times(1)).existsByUserId(USER_ID);
    }
}
