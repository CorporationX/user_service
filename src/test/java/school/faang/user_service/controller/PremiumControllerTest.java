package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.service.PremiumService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {

    @InjectMocks
    private PremiumController premiumController;

    @Mock
    private PremiumService premiumService;

    private long userId;
    private int days;
    private String currency;
    private PremiumDto premiumDto;

    @BeforeEach
    void setUp() {
        userId = 0;
        days = 0;
        currency = "USD";
        premiumDto = new PremiumDto(
            null,
            userId,
            null,
            null
        );
    }

    @Test
    @DisplayName("Buy Premium Successfully")
    void testBuyPremium() {
        when(premiumService.buyPremium(anyLong(), anyInt(), anyString())).thenReturn(premiumDto);

        PremiumDto result = premiumController.buyPremium(userId, days, currency);

        verify(premiumService).buyPremium(anyLong(), anyInt(), anyString());

        assertNotNull(result);
        assertEquals(premiumDto, result);
    }
}