package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.premiun.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceImplTest {

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Spy
    private PremiumMapper mapper = Mappers.getMapper(PremiumMapper.class);

    @Mock
    private PremiumValidator validator;

    @InjectMocks
    private PremiumServiceImpl service;

    @Captor
    private ArgumentCaptor<PaymentRequest> premiumRequestCaptor;

    @Captor
    private ArgumentCaptor<Premium> premiumCaptor;

    private final long userId = 1L;
    private final long premiumId = 2L;
    private final int days = 30;
    private User user;
    private Premium premium;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .build();
        premium = Premium.builder()
                .id(premiumId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .user(user)
                .build();

        when(premiumRepository.existsByUserId(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(premiumRepository.save(any(Premium.class))).thenReturn(premium);
    }

    @Test
    void buyPremium() {
        PremiumDto correctPremiumDto = new PremiumDto(premiumId, userId);
        PaymentRequest correctPaymentRequest = new PaymentRequest(userId, new BigDecimal(10), Currency.USD);

        PremiumDto result = service.buyPremium(userId, days, Currency.USD);

        verifyAll();
        assertEquals(correctPremiumDto, result);
        assertEquals(correctPaymentRequest, premiumRequestCaptor.getValue());
        assertPremium(premiumCaptor.getValue());
    }

    private void assertPremium(Premium premium) {
        int correctAnswer = 30;
        int endDate = premium.getEndDate().getDayOfYear();
        int startDate = this.premium.getStartDate().getDayOfYear();

        assertEquals(correctAnswer, endDate - startDate);
        assertEquals(userId, premium.getUser().getId());
    }

    private void verifyAll() {
        verify(premiumRepository).existsByUserId(userId);
        verify(validator).validate(eq(userId), anyBoolean());
        verify(userRepository).findById(userId);
        verify(paymentServiceClient).processPayment(premiumRequestCaptor.capture());
        verify(premiumRepository).save(premiumCaptor.capture());
        verify(mapper).toDto(any(Premium.class));
    }
}