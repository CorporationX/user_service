package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.client.Currency;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.premium.PremiumValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PremiumRepository premiumRepository;

    private final UserService userService;

    private final PremiumMapper mapper;

    private final PremiumValidator validator;

    private final PaymentServiceClient serviceClient;

    public PremiumDto buyPremium(long id, PremiumPeriod period) {
        validator.validate(id);
        PaymentRequest request = new PaymentRequest(new Random().nextLong(), BigDecimal.valueOf(period.getCost()), Currency.USD);
        PaymentResponse response = serviceClient.sendPayment(request);
        if (response.status().equals("SUCCESS")) {
            LocalDateTime today = LocalDateTime.now();
            Premium premium = Premium.builder().user(userService.findUserById(id))
                    .startDate(today)
                    .endDate(today.plusDays(period.getDays())).build();
            return mapper.toDto(premiumRepository.save(premium));
        } else {
            throw new RuntimeException("Возникла проблема при оплает подписки. Пожалуйста попробуйте оплатить еще раз");
        }
    }

    @Scheduled(cron = "0 0 1 * * *")
    private void deletePremium() {
        premiumRepository.deleteAllExpiredPremium();
    }
}
