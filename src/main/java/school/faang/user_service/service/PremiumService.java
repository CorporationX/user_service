package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final UserService userService;
    private final UserMapper userMapper;

    private static long paymentNumber;

    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("Пользователь уже имеет премиум подписку");
        }

        //Нужно сделать нормальную генерацию номера платежа
        PaymentRequest paymentRequest = new PaymentRequest(++paymentNumber, BigDecimal.valueOf(premiumPeriod.getPrice()), Currency.USD);

        PaymentResponse paymentResponse = paymentServiceClient.sendPayment(paymentRequest);

        if (paymentResponse.getStatus() == PaymentStatus.SUCCESS) {
            Premium premium = new Premium();
            premium.setUser(userMapper.toEntity(userService.getUser(userId)));
            premium.setStartDate(LocalDateTime.now());
            premium.setEndDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()));

            premiumRepository.save(premium);

            return premiumMapper.toDto(premium);
        } else {
            throw new DataValidationException("Ошибка платежа");
        }
    }
}
