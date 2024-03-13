package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.PremiumBoughtEvent;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.PaymentProcessingException;
import school.faang.user_service.integration.payment.PaymentService;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.PremiumBoughtEventPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PaymentService paymentService;
    @Mock
    private PremiumMapper premiumMapper;
    @Mock
    private PremiumBoughtEventPublisher premiumBoughtEventPublisher;
    @InjectMocks
    private PremiumService premiumService;

    private UserDto userDto;
    private User user;
    private Premium premium;
    private PremiumDto premiumDto;
    private int days;

    @BeforeEach
    void setUp() {
        long userId = 1l;
        days = 30;
        userDto = UserDto.builder()
                .id(userId)
                .build();

        user = User.builder()
                .id(userId)
                .build();

        premium = Premium.builder()
                .id(1L)
                .user(user)
                .build();

        premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(userId)
                .build();
    }


    @Test
    void testShouldBuyPremium() {
        when(userService.getUserById(user.getId())).thenReturn(userDto);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(premiumRepository.existsByUserId(user.getId())).thenReturn(false);
        doNothing().when(paymentService).makePayment(any(PremiumPeriod.class));
        when(premiumRepository.save(any(Premium.class))).thenReturn(premium);
        when(premiumMapper.toDto(any(Premium.class))).thenReturn(premiumDto);
        premiumService.buyPremiumSubscription(user.getId(), days);
        verify(premiumBoughtEventPublisher).publish(any(PremiumBoughtEvent.class));

    }


    @Test
    void testShouldThrowPaymentProcessingExceptionWhenUserHasPremiumStatus() {
        when(userService.getUserById(user.getId())).thenReturn(userDto);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(premiumRepository.existsByUserId(user.getId())).thenReturn(true);
        assertThrows(PaymentProcessingException.class, () -> premiumService.buyPremiumSubscription(user.getId(), days));
    }


}
