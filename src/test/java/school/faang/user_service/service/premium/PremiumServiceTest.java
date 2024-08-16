package school.faang.user_service.service.premium;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.client.Currency;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private UserService userService;
    @Mock
    private PremiumMapper mapper;
    @Mock
    private PaymentServiceClient serviceClient;

    @InjectMocks
    private PremiumService premiumService;

    private long id = 1;

    private PremiumPeriod period = PremiumPeriod.MONTH;

    private PaymentRequest request = new PaymentRequest(new Random().nextLong(), BigDecimal.valueOf(period.getCost()), Currency.USD);
    private PaymentResponse responseSuccess = new PaymentResponse("SUCCESS", 1, 214461,  BigDecimal.valueOf(period.getCost()), Currency.USD, Currency.USD, "");
    private PaymentResponse responseFail = new PaymentResponse("ERROR", 1, 214461,  BigDecimal.valueOf(period.getCost()), Currency.USD, Currency.USD, "");
    private User user = User.builder().id(id).build();
    private Premium premium = Premium.builder().id(0).user(user).startDate(LocalDateTime.now()).startDate(LocalDateTime.now().plusDays(period.getDays())).build();
    private PremiumDto premiumDto = new PremiumDto(0, user.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(period.getDays()));

    @Test
    void buyPremiumForUserWithPremium() {
        Mockito.when(premiumRepository.existsByUserId(id)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> premiumService.buyPremium(id, period));
    }

    @Test
    void buyPremiumSuccess() {
        Mockito.when(premiumRepository.existsByUserId(id)).thenReturn(false);
        Mockito.when(serviceClient.sendPayment(Mockito.any())).thenReturn(responseSuccess);
        Mockito.when(userService.findUserById(id)).thenReturn(user);
        Mockito.when(premiumRepository.save(Mockito.any())).thenReturn(premium);
        Mockito.when(mapper.toDto(Mockito.any())).thenReturn(premiumDto);
        PremiumDto actualResult = premiumService.buyPremium(id, period);
        MatcherAssert.assertThat(actualResult, hasProperty("premiumId", equalTo(0L)));
        MatcherAssert.assertThat(actualResult, hasProperty("userId", equalTo(1L)));
    }

    @Test
    void buyPremiumError() {
        Mockito.when(premiumRepository.existsByUserId(id)).thenReturn(false);
        Mockito.when(serviceClient.sendPayment(Mockito.any())).thenReturn(responseFail);
        assertThrows(RuntimeException.class, () -> premiumService.buyPremium(id, period));
    }
}