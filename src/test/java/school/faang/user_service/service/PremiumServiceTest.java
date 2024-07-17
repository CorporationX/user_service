package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.paymentService.PaymentServiceClient;
import school.faang.user_service.client.paymentService.model.Currency;
import school.faang.user_service.client.paymentService.model.PaymentResponse;
import school.faang.user_service.client.paymentService.model.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.premium.PremiumIllegalArgumentException;
import school.faang.user_service.mapper.premium.PremiumMapperImpl;
import school.faang.user_service.model.premium.PremiumPeriod;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Evgenii Malkov
 */
@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    @InjectMocks
    private PremiumService premiumService;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private PremiumMapperImpl mapper;
    private final Currency currency = Currency.USD;
    private User user;

    private static final long USER_ID = 1L;

    @BeforeEach
    public void init() {
        user = User.builder().id(USER_ID).username("TEST_USERNAME").build();
    }

    @Test
    void testBuyPremiumSuccessful() {
        when(userRepository.findById(USER_ID)).thenReturn(java.util.Optional.of(user));

        PremiumPeriod period = PremiumPeriod.MONTH;

        Premium savedPremium = new Premium(
                1, user, LocalDateTime.now(), LocalDateTime.now().plusDays(period.getDays()));
        when(premiumRepository.save(any(Premium.class))).thenReturn(savedPremium);

        PremiumDto premiumDto = premiumService.buyPremium(USER_ID, period);

        long daysBetween = ChronoUnit.DAYS.between(premiumDto.getStartDate(), premiumDto.getEndDate());
        Assertions.assertNotNull(premiumDto);
        Assertions.assertEquals(USER_ID, premiumDto.getUserId());
        Assertions.assertEquals(period.getDays(), daysBetween);

        verify(premiumRepository).save(any(Premium.class));
    }

    @Test
    public void testBuyPremiumUserAlreadyHasPremium() {
        when(userRepository.findById(USER_ID)).thenReturn(java.util.Optional.of(user));
        when(premiumRepository.existsByUserId(USER_ID)).thenReturn(true);

        PremiumPeriod period = PremiumPeriod.YEAR;

        Assertions.assertThrows(PremiumIllegalArgumentException.class,
                () -> premiumService.buyPremium(USER_ID, period));
    }
}
