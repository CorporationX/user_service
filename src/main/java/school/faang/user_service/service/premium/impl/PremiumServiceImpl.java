package school.faang.user_service.service.premium.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.entity.User;
import school.faang.user_service.dto.entity.premium.Premium;
import school.faang.user_service.dto.entity.premium.PremiumPeriod;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.util.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private static final String INTEGRATION_ERR_MSG = "Ошибка взаимодействия с сервисом оплат!";

    @Override
    @Transactional
    public PremiumDto buyPremium(long userid, PremiumPeriod premiumPeriod) {
        User user = userRepository.findById(userid).orElseThrow(() ->
                new IllegalArgumentException(String.format("Пользователь с id: %s не найден", userid)));

        if (premiumRepository.existsByUserId(userid)) {
            throw new IllegalArgumentException(
                    String.format("У пользователя с id: %s уже есть премиум-доступ", userid));
        }

        PaymentResponse paymentResponse = sendPayment(premiumPeriod.getPrice(), Currency.USD);
        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new IllegalArgumentException("Оплата не прошла!Повторите попытку!");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        premiumRepository.save(Premium.builder()
                .user(user)
                .startDate(currentDateTime)
                .endDate(currentDateTime.plusMonths(premiumPeriod.getMonths()))
                .build());

        return null;
    }

    private PaymentResponse sendPayment(@NotNull BigDecimal amount, @NotNull Currency currency) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Amount не может быть 0");
        }
        if (!Arrays.asList(Currency.values()).contains(currency)) {
            throw new IllegalArgumentException("Неверный параметр currency");
        }
        Random random = new Random();
        try {
            ResponseEntity<String> responseEntity = paymentServiceClient.pay(
                    new PaymentRequest(random.nextInt(), amount, currency));
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String response = responseEntity.getBody();
                ObjectMapper mapper = Utils.createJsonMapper();
                PaymentResponse paymentResponse = mapper.readValue(responseEntity.getBody(), PaymentResponse.class);
                log.debug("paymentResponse response:{} {}", responseEntity.getStatusCode(), response);
                return paymentResponse;
            } else {
                log.warn("paymentResponse response:{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
                throw new IllegalArgumentException(responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR ? INTEGRATION_ERR_MSG : String.valueOf(responseEntity.getBody()));
            }
        } catch (FeignException e) {
            log.error("paymentResponse response:{}", e.toString());
            throw new IllegalArgumentException(INTEGRATION_ERR_MSG);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(INTEGRATION_ERR_MSG);
        }
    }
}