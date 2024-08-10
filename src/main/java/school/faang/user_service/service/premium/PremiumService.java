package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment_service.Currency;
import school.faang.user_service.client.payment_service.PaymentPostPayRequestDto;
import school.faang.user_service.client.payment_service.PaymentPostPayResponseDto;
import school.faang.user_service.client.payment_service.PaymentServiceClient;
import school.faang.user_service.client.payment_service.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;
    private final ExecutorService executor;

    private static boolean isValidResponse(ResponseEntity<PaymentPostPayResponseDto> response) {
        return response.getStatusCode() == HttpStatus.OK &&
               response.getBody() != null &&
               response.getBody().status() == PaymentStatus.SUCCESS;
    }

    @Transactional
    public PremiumDto buyPremium(@NonNull final Long userId, @NonNull final Integer days) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User not found"));

        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("User already has premium");
        }

        payForPremium(days);

        Premium premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(days))
                .build();

        return premiumMapper.toDto(premiumRepository.save(premium));
    }

    @Transactional
    public void removePremium(@NonNull final Long userId) {
        premiumRepository.deleteByUserId(userId);
    }

    @Scheduled(cron = "0 0 0 * * ?") // every day at midnight
    @Transactional
    public void removeExpiredUsers() {
        List<Premium> expiredUsers = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        expiredUsers.forEach(expiredPremiumUser -> {
            this.removePremium(expiredPremiumUser.getUser().getId());
        });
    }

    private void payForPremium(final Integer days) {
        PremiumPeriod period = PremiumPeriod.getPremiumPeriod(days);
        PaymentPostPayRequestDto requestDto = new PaymentPostPayRequestDto(1, period.getPrice(), Currency.USD);
        ResponseEntity<PaymentPostPayResponseDto> response = paymentServiceClient.pay(requestDto);

        if (!isValidResponse(response)) {
            throw new RuntimeException("Payment failed");
        }
    }

    @Transactional
    public void removePremiums(final int batch) {
        List<Premium> premiumsForDelete = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        if (!premiumsForDelete.isEmpty()) {
            List<Long> premiumIds = premiumsForDelete.stream()
                            .map(Premium::getId)
                            .toList();
            premiumRepository.deleteByIds(premiumIds);
            log.info(LocalDateTime.now() + " Удалены премиумы\n" + premiumsForDelete);
        } else {
            log.info(LocalDateTime.now() + " Премиумы для удаления отсутствуют");
        }
    }
}
