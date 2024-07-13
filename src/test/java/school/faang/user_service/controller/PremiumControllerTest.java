package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.PremiumPeriod;
import school.faang.user_service.exception.IllegalEntityException;
import school.faang.user_service.service.PremiumService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {

    @InjectMocks
    private PremiumController premiumController;

    @Mock
    private PremiumService premiumService;

    private long userId;
    private int validDays;
    private int invalidDays;
    private PremiumDto premiumDto;

    @BeforeEach
    void setUp() {
        userId = 0;
        validDays = 30;
        invalidDays = 0;
        premiumDto = new PremiumDto(
            null,
            userId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        reset(premiumService);
    }

    @Test
    @DisplayName("Buy Premium Successfully")
    void testBuyPremium() {
        when(premiumService.buyPremium(userId, PremiumPeriod.fromDays(validDays))).thenReturn(premiumDto);

        PremiumDto result = premiumController.buyPremium(userId, validDays);

        verify(premiumService).buyPremium(userId, PremiumPeriod.fromDays(validDays));

        assertNotNull(result);
        assertEquals(premiumDto, result);
    }

    @Test
    @DisplayName("Buy Premium Throws IllegalSubscriptionException")
    void testBuyPremiumIllegalSubscriptionException() {
        assertThrows(IllegalEntityException.class, () -> premiumController.buyPremium(userId, invalidDays));
    }
}