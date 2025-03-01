package school.faang.user_service.service.premium.impl;

import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.entity.premium.PremiumPeriod;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.CheckException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;
    private static final String INTEGRATION_ERR_MSG = "Ошибка взаимодействия с сервисом оплат!";

    @Override
    @Transactional
    public PremiumDto buyPremium(long userid, long paymentNumber, PremiumPeriod premiumPeriod) {
        User user = userRepositoryAdapter.getUserById(userid);

        if (premiumRepository.existsByUserIdAndEndDateGreaterThan(userid, LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    String.format("У пользователя с id: %s уже есть премиум-доступ", userid));
        }

        PaymentResponse paymentResponse = sendPayment(premiumPeriod.getPrice(), Currency.USD, paymentNumber);
        if (paymentResponse.getStatus() != PaymentStatus.SUCCESS) {
            throw new CheckException("Оплата не прошла!Повторите попытку!");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        return premiumMapper.toDto(
                premiumRepository.save(Premium.builder()
                        .user(user)
                        .startDate(currentDateTime)
                        .endDate(currentDateTime.plusMonths(premiumPeriod.getMonths()))
                        .build()));
    }

    private PaymentResponse sendPayment(@NotNull BigDecimal amount, @NotNull Currency currency, long paymentNumber) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Amount не может быть 0");
        }
        if (!Arrays.asList(Currency.values()).contains(currency)) {
            throw new IllegalArgumentException("Неверный параметр currency");
        }
        try {
            ResponseEntity<PaymentResponse> responseEntity = paymentServiceClient.pay(
                    new PaymentRequest(paymentNumber, amount, currency));
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.debug("paymentResponse response:{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
                return responseEntity.getBody();
            } else {
                log.warn("paymentResponse response:{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
                throw new IllegalArgumentException(responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR ? INTEGRATION_ERR_MSG : String.valueOf(responseEntity.getBody()));
            }
        } catch (FeignException e) {
            log.error("paymentResponse response:{}", e.toString());
            throw new IllegalArgumentException(INTEGRATION_ERR_MSG);
        }
    }
}