package school.faang.user_service.service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.entity.premium.PaymentRequest;
import school.faang.user_service.entity.premium.PaymentResponse;
import school.faang.user_service.entity.premium.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PremiumRepository premiumRepo;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumValidator premiumValidator;

    @Spy
    private PremiumMapper mapper;

    @InjectMocks
    private PremiumService premiumService;

    private Premium premium = new Premium();
    private User user = new User();
    private PremiumDto premiumDto = new PremiumDto();

    @BeforeEach
    public void init() {
        UserService userService = Mockito.mock(UserService.class);
        PaymentServiceClient paymentServiceClient = Mockito.mock(PaymentServiceClient.class);
        PremiumValidator premiumValidator = Mockito.mock(PremiumValidator.class);
        PremiumMapper mapper = Mockito.mock(PremiumMapper.class);
        PremiumRepository premiumRepo = Mockito.mock(PremiumRepository.class);
        PremiumService premiumService = new PremiumService(premiumRepo, paymentServiceClient,
                premiumValidator, mapper, userService);

        Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        user.setId(1L);
        premium.setUser(user);
        premium.setStartDate(LocalDateTime.now(clock));
        premium.setEndDate(LocalDateTime.now().plusDays(PremiumPeriod.ONE_MONTH.getDays()));
    }

    @Test
    public void testBuyPremiumSuccess() {
        long paymentNum = 10L;
        User user = new User();
        user.setId(1L);
        PaymentResponse response = new PaymentResponse(PaymentStatus.SUCCESS, 10, paymentNum,
                BigDecimal.valueOf(PremiumPeriod.ONE_MONTH.getPrice()), Currency.USD, "Success");
        PaymentRequest request = new PaymentRequest(paymentNum, BigDecimal.valueOf(PremiumPeriod.ONE_MONTH.getPrice()),
                Currency.USD);
        Mockito.when(paymentServiceClient.sendPayment(request)).thenReturn(ResponseEntity.ok(response));
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        premiumService.buyPremium(1L, PremiumPeriod.ONE_MONTH);
        Mockito.verify(paymentServiceClient, Mockito.times(1)).sendPayment(request);
    }

    @Test
    public void testSavePremiumSuccess() {
        premiumDto.setUserId(1L);
        Mockito.lenient().when(premiumService.savePremium(user, PremiumPeriod.ONE_MONTH)).thenReturn(premiumDto);
        Mockito.lenient().when(premiumRepo.save(premium)).thenReturn(premium);

        assertEquals(premium.getId(), premiumDto.getId());
        assertEquals(premium.getUser().getId(), premiumDto.getUserId());
    }
}
