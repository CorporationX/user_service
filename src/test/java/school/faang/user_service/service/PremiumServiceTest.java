package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PremiumAlreadyExistsException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.mapper.mymappers.User1Mapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    @Mock
    private PaymentServiceClient paymentService;
    @Mock
    private UserService userService;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PremiumMapper premiumMapper;
    @Mock
    private User1Mapper userMapper;

    @InjectMocks
    private PremiumService premiumService;

    int userId = 1;
    PaymentRequest paymentRequest = new PaymentRequest(12345L, BigDecimal.valueOf(1000), Currency.USD);
    PaymentResponse paymentResponse = new PaymentResponse(PaymentStatus.SUCCESS, 123, 12345L, BigDecimal.valueOf(1000), Currency.USD, "Payment successful");
    User user;
    UserDto userDto;
    Premium premium;
    PremiumDto premiumDto;

    @BeforeEach
    void setUp() {
        premium = Premium.builder().user(user).startDate(LocalDateTime.now()).build();
        premiumDto = PremiumDto.builder().build();
        user = User.builder().build();
        userDto = UserDto.builder().build();
    }

    @Test
    void PremiumAlreadyExistsExceptionTest() {
        Mockito.when(premiumRepository.existsByUserId(userId)).thenReturn(true);
        Assertions.assertThrows(
                PremiumAlreadyExistsException.class,
                () -> premiumService.buyPremium(userId, 1)
        );
    }
    @Test
    void buyPremiumTest() {
        Mockito.when(userService.getUserEntity(userId)).thenReturn(user);
        Mockito.when(userMapper.toDto(user)).thenReturn(userDto);
        Mockito.when(premiumMapper.toDto(premium)).thenReturn(premiumDto);
        Mockito.when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        Mockito.when(paymentService.sendPayment(Mockito.any())).thenReturn(paymentResponse);
        Mockito.when(premiumRepository.save(Mockito.any(Premium.class))).thenReturn(premium);

        var dto = premiumService.buyPremium(userId, 31);
        System.out.println(dto);
    }


}
