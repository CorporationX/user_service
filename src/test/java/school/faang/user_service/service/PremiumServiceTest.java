package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumValidator premiumValidator;

    @Mock
    private Clock clock;

    @Spy
    private PremiumMapperImpl premiumMapper;

    private PaymentResponse paymentResponse;

    private Premium premium;

    private User user;

    private PremiumDto premiumDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

        premium = new Premium();
        premium.setId(1L);
        premium.setUser(user);
        LocalDateTime startDate = LocalDateTime.now(clock);
        LocalDateTime endDate = startDate.plusDays(PremiumPeriod.THREE_MONTH.getDays());
        premium.setStartDate(startDate);
        premium.setEndDate(endDate);

        premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Test
    void testByPremium() {
        PremiumDto actual = premiumMapper.toDto(premium);

        assertEquals(premiumDto, actual);
    }

    @Test
    void testPaymentPremium() {
        long paymentNumber = 3L;
        PaymentResponse response = new PaymentResponse(
                PaymentStatus.SUCCESS, 1, paymentNumber, new BigDecimal(25), Currency.USD,
                "Success");
        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, new BigDecimal(25), Currency.USD);

        Mockito.when(premiumRepository.count()).thenReturn(paymentNumber);
        Mockito.when(paymentServiceClient.sendPayment(paymentRequest)).thenReturn(ResponseEntity.ok(response));
        ReflectionTestUtils.invokeMethod(premiumService, "paymentPremium", PremiumPeriod.THREE_MONTH);

        Mockito.verify(paymentServiceClient, Mockito.times(1)).sendPayment(paymentRequest);
        Mockito.verify(premiumValidator, Mockito.times(1)).validateResponse(response);
    }

    @Test
    void testSavePremiumToRepository() {
        ReflectionTestUtils.invokeMethod(premiumService, "savePremiumToRepository", PremiumPeriod.THREE_MONTH, user, clock);
        premium.setId(0L);

        Mockito.verify(premiumRepository, Mockito.times(1)).save(premium);
    }
}