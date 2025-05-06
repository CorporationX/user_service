package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.premium.PremiumNotificationDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.premium.PremiumType;
import school.faang.user_service.service.kafka.publisher.KafkaPublisher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumRenewalService {
    private final KafkaPublisher kafkaPublisher;
    private final PremiumBuyingService premiumBuyingService;

    @Value("${spring.kafka.producer.topics.premium.notifications.expired-topic}")
    private String premiumExpiredTopic;

    @Value("${app.premium.expiry-notification-days}")
    private int premiumExpiryNotificationDays;

    @Value("${spring.kafka.producer.topics.premium.notifications.expire-soon-topic}")
    private String premiumExpireSoonTopic;

    @Transactional
    public void updatePremiumForUsers(List<User> users) {
        for (User user : users) {
            Premium premium = user.getPremium();
            LocalDateTime now = LocalDateTime.now();
            if (!premium.isAutoRenew()) {
                PremiumNotificationDto premiumNotificationDto = PremiumNotificationDto.builder()
                        .userId(user.getId())
                        .build();
                if (premium.getEndDate().isAfter(now)) {
                    log.info("Premium for user with ID {} expired", user.getId());
                    user.setPremium(null);
                    kafkaPublisher.sendInTransaction(premiumNotificationDto, premiumExpiredTopic);
                } else if (premium.getEndDate().plusDays(premiumExpiryNotificationDays).isAfter(now)) {
                    kafkaPublisher.sendInTransaction(premiumNotificationDto, premiumExpireSoonTopic);
                }
            } else {
                int monthDuration = (int) ChronoUnit.MONTHS.between(premium.getStartDate(), premium.getEndDate());
                PremiumRequestDto premiumRequestDto = PremiumRequestDto.builder()
                        .premiumType(PremiumType.getByDuration(monthDuration))
                        .userId(user.getId())
                        .selectedCurrency(premium.getCurrency())
                        .autoRenew(premium.isAutoRenew())
                        .build();

                premiumBuyingService.buyPremium(premiumRequestDto, false);
            }
        }
    }
}
