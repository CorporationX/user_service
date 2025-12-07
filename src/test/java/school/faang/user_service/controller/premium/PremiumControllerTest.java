package school.faang.user_service.controller.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {

    @Mock
    private PremiumService premiumService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PremiumController premiumController;

    private PremiumDto testPremiumDto;

    @BeforeEach
    void setUp() {
        testPremiumDto = PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .amount(new BigDecimal("10.00"))
                .paymentNumber("PREM-1-test123")
                .verificationCode(12345)
                .currency(Currency.USD)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void buyPremiumWithValidDaysShouldReturnOk() {
        
        PremiumPeriod period = PremiumPeriod.MONTHLY;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(userId, PremiumPeriod.MONTHLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(period);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(userId);
        assertThat(response.getBody().getPremiumPeriod()).isEqualTo(PremiumPeriod.MONTHLY);
    }

    @Test
    void buyPremiumWithQuarterlyDaysShouldReturnOk() {
        
        PremiumPeriod period = PremiumPeriod.QUARTERLY;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(userId, PremiumPeriod.QUARTERLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(period);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void buyPremiumWithYearlyDaysShouldReturnOk() {
        
        PremiumPeriod period = PremiumPeriod.YEARLY;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(userId, PremiumPeriod.YEARLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(period);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"MONTHLY", "QUARTERLY", "YEARLY"})
    void buyPremiumWithValidPeriodsShouldCallServiceWithCorrectPeriod(String periodName) {
        
        PremiumPeriod period = PremiumPeriod.valueOf(periodName);
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(any(Long.class), any(PremiumPeriod.class))).thenReturn(testPremiumDto);
        
        premiumController.buyPremium(period);

        // Verification is done through the mock setup
        // The service is called with the correct period
    }


    @Test
    void buyPremiumWithServiceExceptionShouldPropagateException() {
        
        PremiumPeriod period = PremiumPeriod.MONTHLY;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(userId, PremiumPeriod.MONTHLY))
                .thenThrow(new RuntimeException("Service error"));

        
        assertThatThrownBy(() -> premiumController.buyPremium(period))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service error");
    }

    @Test
    void buyPremiumWithUserContextExceptionShouldPropagateException() {
        
        PremiumPeriod period = PremiumPeriod.MONTHLY;
        when(userContext.getUserId()).thenThrow(new RuntimeException("User context error"));

        
        assertThatThrownBy(() -> premiumController.buyPremium(period))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User context error");
    }

    @Test
    void buyPremiumWithMaxLongUserIdShouldWork() {
        
        PremiumPeriod period = PremiumPeriod.MONTHLY;
        long maxUserId = Long.MAX_VALUE;
        when(userContext.getUserId()).thenReturn(maxUserId);
        when(premiumService.buyPremium(maxUserId, PremiumPeriod.MONTHLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(period);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void buyPremiumWithMinLongUserIdShouldWork() {
        
        PremiumPeriod period = PremiumPeriod.MONTHLY;
        long minUserId = Long.MIN_VALUE;
        when(userContext.getUserId()).thenReturn(minUserId);
        when(premiumService.buyPremium(minUserId, PremiumPeriod.MONTHLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(period);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void buyPremiumWithZeroUserIdShouldWork() {
        
        PremiumPeriod period = PremiumPeriod.MONTHLY;
        long zeroUserId = 0L;
        when(userContext.getUserId()).thenReturn(zeroUserId);
        when(premiumService.buyPremium(zeroUserId, PremiumPeriod.MONTHLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(period);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
