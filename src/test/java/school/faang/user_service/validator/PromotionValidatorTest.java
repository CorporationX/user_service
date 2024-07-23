package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.AlreadyPurchasedException;
import school.faang.user_service.repository.promotiom.PromotionRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionValidatorTest {
    @InjectMocks
    private PromotionValidator promotionValidator;

    @Mock
    private PromotionRepository promotionRepository;

    private long userId;
    private long eventId;

    @BeforeEach
    void setUp() {
        userId = 0;
        eventId = 0;
    }

    @Test
    void validateUserHasPromotion() {
        when(promotionRepository.existsByUserId(anyLong())).thenReturn(false);

        assertDoesNotThrow(() -> promotionValidator.validateUserAlreadyHasPromotion(userId));
    }

    @Test
    void validateUserHasPromotionThrowsAlreadyPurchasedException() {
        when(promotionRepository.existsByUserId(anyLong())).thenReturn(true);

        assertThrows(AlreadyPurchasedException.class, () -> promotionValidator.validateUserAlreadyHasPromotion(userId));
    }

    @Test
    void validateEventHasPromotion() {
        when(promotionRepository.existsByEventId(anyLong())).thenReturn(false);

        assertDoesNotThrow(() -> promotionValidator.validateEventAlreadyHasPromotion(eventId));
    }

    @Test
    void validateEventHasPromotionThrowsAlreadyPurchasedException() {
        when(promotionRepository.existsByEventId(anyLong())).thenReturn(true);

        assertThrows(AlreadyPurchasedException.class, () -> promotionValidator.validateEventAlreadyHasPromotion(eventId));
    }
}
